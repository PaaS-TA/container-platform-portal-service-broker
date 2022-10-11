package org.openpaas.servicebroker.container.platform.repo;

import org.openpaas.servicebroker.container.platform.model.JpaAdminToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


/**
 * Service Instance JPA Repository 클래스
 * @author Hyerin
 * @since 2018.07.24
 * @version 20180725
 */
@Repository
public interface JpaAdminTokenRepository extends JpaRepository<JpaAdminToken, String>{

}
