package net.naffets.nevsuite.backend.timeseries.core.timeseries;

import net.naffets.nevsuite.backend.timeseries.core.dataprovider.TimeseriesDataProvider;
import net.naffets.nevsuite.backend.timeseries.core.valueplugin.ValuePlugin;

import javax.measure.converter.UnitConverter;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author br4sk4
 * created on 20.03.2016
 */
public class Timeseries<T> {

    protected UUID uuid;
    protected ValuePlugin<T> valuePlugin;
    protected TimeseriesDataProvider<T> dataProvider;

    protected TimeseriesInterval interval;
    protected TimeseriesPeriod period;
    protected TimeseriesUnit unit;

    public Timeseries(
            ValuePlugin<T> valuePlugin,
            TimeseriesDataProvider<T> dataProvider,
            TimeseriesInterval interval) {

        this.uuid = UUID.randomUUID();
        this.dataProvider = dataProvider;
        this.valuePlugin = valuePlugin;

        this.interval = interval;
        this.period = TimeseriesPeriod.MIN15;
        this.unit = TimeseriesUnit.NONE;

    }

    public Timeseries(
            ValuePlugin<T> valuePlugin,
            TimeseriesDataProvider<T> dataProvider,
            TimeseriesInterval interval,
            TimeseriesPeriod period,
            TimeseriesUnit unit) {

        this.uuid = UUID.randomUUID();
        this.dataProvider = dataProvider;
        this.valuePlugin = valuePlugin;

        this.interval = interval;
        this.period = period;
        this.unit = unit;

    }

    protected Timeseries(Timeseries<T> timeseries) {

        this.uuid = timeseries.uuid;
        this.valuePlugin = timeseries.valuePlugin;
        this.dataProvider = timeseries.dataProvider.clone();

        this.interval = timeseries.interval;
        this.period = timeseries.period;
        this.unit = timeseries.unit;

    }

    protected Timeseries(
            Timeseries<T> timeseries,
            HashMap<Instant, T> valueMap) {

        this.uuid = timeseries.uuid;
        this.valuePlugin = timeseries.valuePlugin;
        this.dataProvider = timeseries.dataProvider.clone(valueMap);

        this.interval = timeseries.interval;
        this.period = timeseries.period;
        this.unit = timeseries.unit;

    }

    public void save() {
        dataProvider.save(dataProvider.getValueMap(interval));
    }

    public HashMap<Instant, T> load() {
        return dataProvider.load(this.interval);
    }

    public HashMap<Instant, T> load(TimeseriesInterval interval) {
        this.interval = this.interval.join(interval);
        return dataProvider.load(interval);
    }

    public String getTimeseriesIdentifier() {
        return this.uuid.toString();
    }

    public TimeseriesPeriod getPeriod() {
        return period;
    }

    public TimeseriesUnit getUnit() {
        return unit;
    }

    public TimeseriesInterval getSpan() {
        return interval;
    }

    public Set<Instant> getIntervalSet() {
        return this.interval.getPeriodicIntervalSet(this.period);
    }

    public HashMap<Instant, T> getValueMap() {
        return this.dataProvider.getValueMap();
    }

    public HashMap<Instant, T> getValueMap(TimeseriesInterval interval) {
        HashMap<Instant, T> valueMap = this.dataProvider.getValueMap(interval);
        Set<Instant> intervalSet = this.interval.getPeriodicIntervalSet(this.period);

        intervalSet.stream().filter(timestamp -> !valueMap.containsKey(timestamp))
                .forEach(timestamp -> valueMap.put(timestamp, this.valuePlugin.createZero()));

        return valueMap;
    }

    public T getValue(Instant timestamp) {
        return this.dataProvider.getValue(timestamp);
    }

    public void setValue(Instant timestamp, T value) {
        this.dataProvider.setValue(timestamp, value);
    }

    public Timeseries<T> add(Timeseries<T> timeseries) {

        if (timeseries == null || !this.unit.isCompatible(timeseries.getUnit())) return timeseries;

        Timeseries<T> newTimeseries = this.clone();
        Timeseries<T> ohterTimeseries = this.unit.equals(timeseries.getUnit()) ? timeseries : timeseries.toUnit(this.unit);

        HashMap<Instant, T> valueMap = ohterTimeseries.getValueMap(interval);
        Set<Instant> keySet = valueMap.keySet();

        keySet.forEach(timestamp -> newTimeseries.setValue(timestamp, this.valuePlugin.add(newTimeseries.getValue(timestamp), valueMap.get(timestamp))));

        return newTimeseries;
    }

    public Timeseries<T> subtract(Timeseries<T> timeseries) {

        if (timeseries == null || !this.unit.isCompatible(timeseries.getUnit())) return timeseries;

        Timeseries<T> newTimeseries = this.clone();
        Timeseries<T> ohterTimeseries = this.unit.equals(timeseries.getUnit()) ? timeseries : timeseries.toUnit(this.unit);

        HashMap<Instant, T> valueMap = ohterTimeseries.getValueMap(interval);
        Set<Instant> keySet = valueMap.keySet();

        keySet.forEach(timestamp -> newTimeseries.setValue(timestamp, this.valuePlugin.subtract(newTimeseries.getValue(timestamp), valueMap.get(timestamp))));

        return newTimeseries;
    }

    public Timeseries<T> multiply(Timeseries<T> timeseries) {

        if (timeseries == null) return null;

        Timeseries<T> newTimeseries = this.clone();

        HashMap<Instant, T> valueMap = timeseries.getValueMap(interval);
        Set<Instant> keySet = valueMap.keySet();

        keySet.forEach(timestamp -> newTimeseries.setValue(timestamp, this.valuePlugin.multiply(newTimeseries.getValue(timestamp), valueMap.get(timestamp))));

        newTimeseries.unit = this.unit.equals(timeseries.getUnit()) ? this.unit : TimeseriesUnit.ofMeasurementUnit(newTimeseries.unit.toMeasurementUnit().times(timeseries.getUnit().toMeasurementUnit()));

        return newTimeseries;
    }

    public Timeseries<T> divide(Timeseries<T> timeseries) {

        if (timeseries == null) return null;

        Timeseries<T> newTimeseries = this.clone();

        HashMap<Instant, T> valueMap = timeseries.getValueMap(interval);
        Set<Instant> keySet = valueMap.keySet();

        keySet.forEach(timestamp -> newTimeseries.setValue(timestamp, this.valuePlugin.divide(newTimeseries.getValue(timestamp), valueMap.get(timestamp))));

        newTimeseries.unit = this.unit.equals(timeseries.getUnit()) ? this.unit : TimeseriesUnit.ofMeasurementUnit(newTimeseries.unit.toMeasurementUnit().divide(timeseries.getUnit().toMeasurementUnit()));

        return newTimeseries;
    }

    public Timeseries<T> min(Timeseries<T> timeseries) {

        if (timeseries == null || !this.unit.isCompatible(timeseries.getUnit())) return timeseries;

        Timeseries<T> newTimeseries = this.clone();
        Timeseries<T> ohterTimeseries = this.unit.equals(timeseries.getUnit()) ? timeseries : timeseries.toUnit(this.unit);

        HashMap<Instant, T> valueMap = ohterTimeseries.getValueMap(interval);
        Set<Instant> keySet = valueMap.keySet();

        keySet.forEach(timestamp -> newTimeseries.setValue(timestamp, this.valuePlugin.min(newTimeseries.getValue(timestamp), valueMap.get(timestamp))));

        return newTimeseries;

    }

    public Timeseries<T> max(Timeseries<T> timeseries) {

        if (timeseries == null || !this.unit.isCompatible(timeseries.getUnit())) return timeseries;

        Timeseries<T> newTimeseries = this.clone();
        Timeseries<T> ohterTimeseries = this.unit.equals(timeseries.getUnit()) ? timeseries : timeseries.toUnit(this.unit);

        HashMap<Instant, T> valueMap = ohterTimeseries.getValueMap(interval);
        Set<Instant> keySet = valueMap.keySet();

        keySet.forEach(timestamp -> newTimeseries.setValue(timestamp, this.valuePlugin.max(newTimeseries.getValue(timestamp), valueMap.get(timestamp))));

        return newTimeseries;

    }

    public Timeseries<T> toUnit(TimeseriesUnit unit) {
        Timeseries<T> newTimeseries = this.clone();

        HashMap<Instant, T> valueMap = newTimeseries.getValueMap(interval);
        Set<Instant> keySet = valueMap.keySet();

        if (this.unit.isTimeintegralOf(unit)) {
            UnitConverter uc = this.unit.extractBaseunit().getConverterTo(unit.toMeasurementUnit());
            keySet.forEach(timestamp -> newTimeseries.setValue(timestamp, this.valuePlugin.create(uc.convert(this.valuePlugin.doubleValue(valueMap.get(timestamp)) / this.period.getTimeintegralFactor(this.unit.extractTimeunit(), timestamp.atZone(this.interval.zoneId))))));
        } else if (unit.isTimeintegralOf(this.unit)) {
            UnitConverter uc = this.unit.toMeasurementUnit().getConverterTo(unit.extractBaseunit());
            keySet.forEach(timestamp -> newTimeseries.setValue(timestamp, this.valuePlugin.create(uc.convert(this.valuePlugin.doubleValue(valueMap.get(timestamp)) * this.period.getTimeintegralFactor(unit.extractTimeunit(), timestamp.atZone(this.interval.zoneId))))));
        } else if (this.unit.isCompatible(unit)) {
            UnitConverter uc = this.unit.toMeasurementUnit().getConverterTo(unit.toMeasurementUnit());
            keySet.forEach(timestamp -> newTimeseries.setValue(timestamp, this.valuePlugin.create(uc.convert(this.valuePlugin.doubleValue(valueMap.get(timestamp))))));
        }

        newTimeseries.unit = unit;

        return newTimeseries;
    }

    public Timeseries<T> toPeriod(TimeseriesPeriod period) {

        if (period == null) return this;

        if (this.period.compareTo(period) > 0) return spread(period);
        else if (this.period.compareTo(period) < 0) return compact(period);
        else return this.clone();

    }

    protected Timeseries<T> spread(TimeseriesPeriod period) {
        Timeseries<T> newTimeseries = this.clone(new HashMap<>());
        Long newValueCount = this.period.toSeconds(this.interval.getZonedTimestampFrom()) / period.toSeconds(this.interval.getZonedTimestampFrom());

        Set<Instant> keySet = this.getValueMap(this.interval).keySet();
        keySet.forEach(timestamp -> {
            List<T> spreadedValues = this.valuePlugin.spread(this.getValue(timestamp), newValueCount, this.unit.getSpreadType());
            Instant newTimestamp = timestamp;
            int index = 0;

            for (T value : spreadedValues) {
                newTimestamp = (index == 0) ? newTimestamp : newTimestamp.plus(period.toTemporalAmount());
                newTimeseries.setValue(newTimestamp, value);
                index++;
            }
        });

        newTimeseries.period = period;

        return newTimeseries;
    }

    protected Timeseries<T> compact(TimeseriesPeriod period) {
        Timeseries<T> newTimeseries = this.clone(new HashMap<>());
        Long newValueCount = period.toSeconds(this.interval.getZonedTimestampFrom()) / this.period.toSeconds(this.interval.getZonedTimestampFrom());

        List<Instant> keySet = new ArrayList<>(new TreeSet<>(this.getValueMap(this.interval).keySet()));
        Stream.iterate(keySet, l -> l.subList(newValueCount.intValue(), l.size()))
                .limit(keySet.size() / newValueCount + Math.min(keySet.size() % newValueCount, 1))
                .map(l -> l.get(0))
                .collect(Collectors.toSet())
                .forEach(timestamp -> {
                    Instant newTimestamp = null;

                    LinkedList<T> compactedValues = new LinkedList<>();
                    for (int index = 1; index <= newValueCount; index++) {
                        if (index == 1) {
                            newTimestamp = timestamp;
                            compactedValues.addLast(this.getValue(timestamp));
                        } else if (index == newValueCount) {
                            compactedValues.addLast(this.getValue(timestamp));
                            newTimeseries.setValue(newTimestamp, this.valuePlugin.compact(compactedValues, this.unit.getCompactType()));
                            compactedValues = new LinkedList<>();
                        } else {
                            compactedValues.addLast(this.getValue(timestamp));
                        }

                    }
                });

        newTimeseries.period = period;

        return newTimeseries;
    }

    @SuppressWarnings({"CloneDoesntCallSuperClone", "CloneDoesntDeclareCloneNotSupportedException"})
    protected Timeseries<T> clone() {
        return new Timeseries<>(this);
    }

    protected Timeseries<T> clone(HashMap<Instant, T> valueMap) {
        return new Timeseries<>(this, valueMap);
    }

}
