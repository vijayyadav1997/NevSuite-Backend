/**
 * Created by Braska on 11.11.2017.
 */
import React from 'react';

export default class ComboBox extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            showDropdownContent: false,
            selectedValue: '',
            selectedIndex: -1
        };
    }

    render() {
        let $dropdownList;
        let $dropdownItems;

        let toggleDropdownContent = () => this.setState({
            showDropdownContent: !this.state.showDropdownContent,
            selectedIndex: this.state.selectedIndex,
            selectedValue: this.state.selectedValue
        });

        if ( this.state.showDropdownContent ) {
            if (this.props.data.length > 0) $dropdownItems = [];

            let x;
            for (x = 0; x < this.props.data.length; ++x)  {
                const index = x;
                const value = this.props.data[x];
                let handleChange = () => this.setState({
                    showDropdownContent: !this.state.showDropdownContent,
                    selectedIndex: index,
                    selectedValue: value
                });

                $dropdownItems.push(
                    <div key={x} className="dropdownItemContainer"><div className="dropdownItem" onClick={handleChange}>{this.props.data[x]}</div></div>
                );
            }

            let $searchBox=[];
            if ( this.props.showSearchBox ) {
                $searchBox.push(
                    <div className="dropdownSearchbox" ref="searchbox" tabIndex="0">
                        <input className="widget" type="text"/>
                    </div>
                );
                $searchBox.push(<div style={{height: "3px"}} />);
            }

            $dropdownList = (
                <div className="dropdownContent">
                    {$searchBox}
                    {$dropdownItems}
                </div>
            );
        }

        return (
            <div style={{width: "500px", float: "left"}}>
                <div className="widget" tabIndex="0" onBlur={() => {if ( this.state.showDropdownContent && !this.props.showSearchBox) toggleDropdownContent();}}>
                    <div className="comboboxTextfield" onClick={toggleDropdownContent}>
                        <span className="comboboxText">{this.state.selectedValue}</span>
                        <input type="hidden" name="selectedValue" value={this.state.selectedValue}/>
                    </div>
                    <div className="comboboxButton" onClick={toggleDropdownContent}><span className="glyphicon glyphicon-chevron-down comboboxIcon" /></div>
                    {$dropdownList}
                </div>
            </div>
        );
    }
}