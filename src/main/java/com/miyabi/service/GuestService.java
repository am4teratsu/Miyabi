package com.miyabi.service;

import java.util.List;
import org.springframework.stereotype.Service;

import com.miyabi.models.Guest;
import com.miyabi.repository.GuestRepository;

@Service
public class GuestService {

    private final GuestRepository guestRepository;

    public GuestService(GuestRepository guestRepository) {
        this.guestRepository = guestRepository;
    }

    public List<Guest> findAll() {
        return guestRepository.findAll();
    }

    public Guest findById(Integer id) {
        return guestRepository.findById(id).orElse(null);
    }

    public Guest findByDni(String dni) {
        return guestRepository.findByDni(dni).orElse(null);
    }

    public Guest save(Guest guest) {
        return guestRepository.save(guest);
    }
    
	public Guest authenticate(String email, String password) {
		Guest guest = guestRepository.findByEmail(email).orElse(null);

		if (guest != null && guest.getPassword().equals(password)) {
			return guest;
		}
		return null; 
	}
}