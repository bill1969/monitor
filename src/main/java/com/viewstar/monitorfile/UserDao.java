package com.viewstar.monitorfile;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDao extends  CrudRepository<User, String> {
    @Query("from User where userid =?1")
    public User getUserByUserID(String UserID);
}
