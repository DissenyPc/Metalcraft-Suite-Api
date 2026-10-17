package com.carpinteria.api.service;

import com.carpinteria.api.exception.ResourceNotFoundException;
import com.carpinteria.api.model.Client;
import com.carpinteria.api.repository.ClientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ClientService {

    private final ClientRepository repository;

    public ClientService(ClientRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<Client> getAll(String query) {
        if (query != null && !query.isBlank()) {
            return repository.search(query.trim());
        }
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Client getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "id", id));
    }

    @Transactional(readOnly = true)
    public Client getByTaxId(String taxId) {
        return repository.findByTaxId(taxId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "NIF/CIF", taxId));
    }

    public Client create(Client client) {
        if (client.getTaxId() != null && !client.getTaxId().isBlank()
                && repository.findByTaxId(client.getTaxId()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un cliente con el CIF/NIF: " + client.getTaxId());
        }
        return repository.save(client);
    }

    public Client update(Long id, Client updated) {
        Client existing = getById(id);

        if (updated.getTaxId() != null && !updated.getTaxId().isBlank()
                && !updated.getTaxId().equalsIgnoreCase(existing.getTaxId())
                && repository.findByTaxId(updated.getTaxId()).isPresent()) {
            throw new IllegalArgumentException("Ya existe otro cliente con el CIF/NIF: " + updated.getTaxId());
        }

        existing.setName(updated.getName());
        existing.setCompanyName(updated.getCompanyName());
        existing.setTaxId(updated.getTaxId());
        existing.setEmail(updated.getEmail());
        existing.setPhone(updated.getPhone());
        existing.setAddress(updated.getAddress());
        existing.setCity(updated.getCity());
        existing.setPostalCode(updated.getPostalCode());
        existing.setProvince(updated.getProvince());
        existing.setNotes(updated.getNotes());
        if (updated.getActive() != null) {
            existing.setActive(updated.getActive());
        }

        return repository.save(existing);
    }

    public void delete(Long id) {
        Client existing = getById(id);
        repository.delete(existing);
    }
}
