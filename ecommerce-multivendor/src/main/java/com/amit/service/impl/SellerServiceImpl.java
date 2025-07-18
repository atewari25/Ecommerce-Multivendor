package com.amit.service.impl;

import com.amit.config.JwtProvider;
import com.amit.domain.AccountStatus;
import com.amit.domain.USER_ROLE;
import com.amit.model.Address;
import com.amit.model.Seller;
import com.amit.repository.AddressRepository;
import com.amit.repository.SellerRepository;
import com.amit.service.SellerService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SellerServiceImpl implements SellerService {
    private final SellerRepository sellerRepository;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;
    private final AddressRepository addressRepository;


    @Override
    public Seller getSellerProfile(String jwt) throws Exception {
        String email = jwtProvider.getEmailFromJwtToken(jwt);
        return this.getSellerByEmail(email);
    }

    @Override
    public Seller createSeller(Seller seller) throws Exception {
        Seller sellerExists = sellerRepository.findByEmail(seller.getEmail());
        if(sellerExists!=null){
            throw new Exception("email already in use, use different email");
        }
        Address address = addressRepository.save(seller.getPickupAddress());
        Seller newSeller = new Seller();
        newSeller.setEmail(seller.getEmail());
        newSeller.setPassword(passwordEncoder.encode(seller.getPassword()));
        newSeller.setSellerName(seller.getSellerName());
        newSeller.setPickupAddress(address);
        newSeller.setGSTIN(seller.getGSTIN());
        newSeller.setRole(USER_ROLE.SELLER);
        newSeller.setMobile(seller.getMobile());
        newSeller.setBankDetails(seller.getBankDetails());
        newSeller.setBusinessDetails(seller.getBusinessDetails());

        return sellerRepository.save(newSeller);

    }

    @Override
    public Seller getSellerById(Long id) throws  Exception{
        return sellerRepository.findById(id)
                .orElseThrow(()-> new Exception("Seller not found with id: " + id));
    }

    @Override
    public Seller getSellerByEmail(String email) throws Exception{
       Seller seller = sellerRepository.findByEmail(email);
       if(seller == null){
           throw new Exception("Seller not found with email: " + email);
       }
       return seller;
    }

    @Override
    public List<Seller> getAllSellers(AccountStatus status) {
        return sellerRepository.findByAccountStatus(status);
    }

    @Override
    public Seller updateSeller(Long id, Seller seller) throws Exception {
        Seller existingSeller = this.getSellerById(id);
        if(seller.getSellerName()!=null){
            existingSeller.setSellerName(seller.getSellerName());
        }
        if(seller.getEmail()!=null){
            existingSeller.setEmail(seller.getEmail());
        }
        if(seller.getMobile()!=null){
            existingSeller.setMobile(seller.getMobile());
        }
        if(seller.getBusinessDetails()!=null){
            existingSeller.setBusinessDetails(seller.getBusinessDetails());
        }
        if(seller.getBankDetails()!=null &&
                seller.getBankDetails().getAccountHolderName()!=null &&
                seller.getBankDetails().getIfscCode()!=null &&
                seller.getBankDetails().getAccountNumber()!=null
                ){
            existingSeller.setBankDetails(seller.getBankDetails());
            existingSeller.getBankDetails().setAccountHolderName(seller.getBankDetails().getAccountHolderName());
            existingSeller.getBankDetails().setIfscCode(seller.getBankDetails().getIfscCode());
            existingSeller.getBankDetails().setAccountNumber(seller.getBankDetails().getAccountNumber());

        }
        if(seller.getPickupAddress()!=null &&
                seller.getPickupAddress().getAddress()!=null &&
                seller.getPickupAddress().getState()!=null &&
                seller.getPickupAddress().getMobile()!=null &&
                seller.getPickupAddress().getCity()!=null
               ){
            existingSeller.setPickupAddress(seller.getPickupAddress());
            existingSeller.getPickupAddress().setAddress(seller.getPickupAddress().getAddress());
            existingSeller.getPickupAddress().setCity(seller.getPickupAddress().getCity());
            existingSeller.getPickupAddress().setState(seller.getPickupAddress().getState());
            existingSeller.getPickupAddress().setMobile(seller.getPickupAddress().getMobile());

        }
        if(seller.getGSTIN()!=null){
            existingSeller.setGSTIN(seller.getGSTIN());
        }
        return sellerRepository.save(existingSeller);

    }

    @Override
    public void deleteSeller(Long id) throws Exception {
        Seller seller = this.getSellerById(id);
        sellerRepository.delete(seller);
    }

    @Override
    public Seller verifyEmail(String email, String otp) throws Exception {
        Seller seller = this.getSellerByEmail(email);
        seller.setEmailVerified(true);
        return sellerRepository.save(seller);
    }

    @Override
    public Seller updateSellerAccountStatus(Long sellerId, AccountStatus status) throws Exception {
        Seller seller = this.getSellerById(sellerId);
        seller.setAccountStatus(status);
        return sellerRepository.save(seller);
    }
}
