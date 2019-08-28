package com.pinyougou.shop.service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.TbSeller;
import com.pinyougou.sellergoods.service.SellerService;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;


public class UserDetailsServiceImpl implements UserDetailsService {

    @Reference
    private SellerService sellerService;

    public void setSellerService(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        ArrayList<SimpleGrantedAuthority> authorities = new ArrayList<>();
//        authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
//         return new User(username,"12345",authorities);
        ArrayList<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        TbSeller seller = sellerService.findOne(username);
        if (seller!=null){
            return new User(seller.getSellerId(), seller.getPassword(), seller.getStatus().equals("1"),
                    true, true, true, authorities);
        }
            return null;
    }
}
