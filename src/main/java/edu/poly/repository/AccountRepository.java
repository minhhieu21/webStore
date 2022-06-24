package edu.poly.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.poly.domain.Account;

public interface AccountRepository extends JpaRepository<Account, String> {

}
