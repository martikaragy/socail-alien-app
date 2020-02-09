package com.telerikacademy.socialalien.controllers.restControllers;

import com.telerikacademy.socialalien.models.Connection;
import com.telerikacademy.socialalien.models.dtos.ConnectionDto;
import com.telerikacademy.socialalien.services.contracts.ConnectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
@RestController
@RequestMapping("/api/connections")
public class ConnectionRestController {
    private ConnectionService connectionService;

    @Autowired
    public ConnectionRestController(ConnectionService connectionService) {
        this.connectionService = connectionService;
    }

    @GetMapping
    public List<Connection> getAll() {
        return connectionService.getAll();
    }

    @GetMapping("/{id}")
    public Connection getById(@Valid @PathVariable int id) {
        return connectionService.findById(id);
    }

    @GetMapping("/getByUserID/{id}")
    public List<Connection> getAllByUserId(@Valid @PathVariable int id) {
        return connectionService.getAllbyUserId(id);
    }

//    @PostMapping("/create")
//    public Connection create(@Valid @RequestBody Connection connection) {
//        connectionService.create(connection);
//        return connectionService.findById(connection.getId());
//    }

    @PostMapping("/")
    public Connection create(@Valid @RequestBody ConnectionDto dto) {
        return connectionService.create(dto.getSenderId(), dto.getReceiverId(), dto.getConnectionStatus());
    }

//    @PutMapping
//    public Connection update(@RequestBody Connection connection) {
//        connectionService.update(connection);
//        return connectionService.findById(connection.getId());
//    }

    @PutMapping("/{id}")
    public Connection update(@PathVariable("id") int connectionId, @RequestBody ConnectionDto dto) {
        return connectionService.update(connectionId, dto.getConnectionStatus());
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") int connectionId) {
        connectionService.delete(connectionId);
    }

//    @DeleteMapping
//    public Connection delete(@RequestBody Connection connection) {
//        connectionService.delete(connection);
//        return connectionService.findById(connection.getId());
//    }

}
