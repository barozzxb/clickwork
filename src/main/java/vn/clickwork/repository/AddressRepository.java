package vn.clickwork.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.clickwork.entity.Address;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

    /**
     * Find all addresses for a specific admin
     *
     * @param adminId The ID of the admin
     * @return List of addresses
     */
    List<Address> findByAdminId(Long adminId);

    /**
     * Find an address by ID and admin ID
     *
     * @param id The address ID
     * @param adminId The admin ID
     * @return Optional containing the address if found
     */
    Optional<Address> findByIdAndAdminId(Long id, Long adminId);

    /**
     * Delete all addresses for a specific admin
     *
     * @param adminId The ID of the admin
     */
    void deleteByAdminId(Long adminId);
}
