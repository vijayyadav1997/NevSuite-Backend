package net.naffets.nevsuite.backend.framework.domain.repository.temporary;

import net.naffets.nevsuite.backend.framework.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author br4sk4 / created on 13.10.2017
 */
@Repository
public interface UserInMemoryRepository extends JpaRepository<User, String> {
}
