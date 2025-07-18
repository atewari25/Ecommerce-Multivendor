package com.amit.repository;

import com.amit.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;

 public interface AddressRepository extends JpaRepository<Address,Long> {

}
