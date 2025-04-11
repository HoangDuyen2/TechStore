package hcmute.edu.vn.techstore.service.impl;

import hcmute.edu.vn.techstore.entity.AccountEntity;
import hcmute.edu.vn.techstore.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;

@Service
public class UserDetailServiceImpl implements UserDetailsService {

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println(username);
        AccountEntity account = accountRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("Email Not Found"));
        System.out.println("Username received: " + username);
        System.out.println("User found: " + account.getPassword());

        return new User(account.getEmail(), account.getPassword(), getAuthorities(account));
    }

    public Collection<? extends GrantedAuthority> getAuthorities(AccountEntity account) {
        String role = account.getUser().getRole().getName().toString();
        return Collections.singletonList(new SimpleGrantedAuthority(role));
    }
}
