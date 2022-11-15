package com.example.ADDB.endpoints;

import com.example.ADDB.sync.servicesync.EmployeeSyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/control", produces = "application/json")
public class SynchronizerController {
    private final EmployeeSyncService personRepoSync;

    @GetMapping("/sync")
    public void synchronize() {
        personRepoSync.syncBegin();
    }



}
