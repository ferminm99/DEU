package natbra.appstarter.server.controllers;

import natbra.appstarter.server.Utils;
import natbra.appstarter.server.model.auth.User;
import natbra.appstarter.server.model.train.Planification;
import natbra.appstarter.server.model.train.TrainerPlanification;
import natbra.appstarter.server.model.train.UserPlanification;
import natbra.appstarter.server.repository.UserPlanificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Controller
public class UserPlanificationController {

    private final String baseUrl = "/admin/users/planifications";

    @Autowired
    UserPlanificationRepository userPlanificationRepository;

    @Autowired
    Utils utils;

    @GetMapping(baseUrl)
    public HttpEntity<List<UserPlanification>> getUserPlanifications(){
        return ResponseEntity.ok(userPlanificationRepository.findAll());
    }

    @DeleteMapping(baseUrl)
    public HttpEntity<List<UserPlanification>> deleteUserPlanification(@RequestBody List<UserPlanification> userPlanifications){

        //aca me llegan los userPlanifications que quiero eliminar, del otro lado elegire cuales son los que saco y creo un array con esos
        userPlanificationRepository.deleteAll(userPlanifications);
        return ResponseEntity.ok(userPlanifications);
    }



    @PostMapping(baseUrl)
    public HttpEntity<List<UserPlanification>> addUserPlanification(@RequestBody List<UserPlanification> userPlanifications){
        return ResponseEntity.ok(userPlanificationRepository.saveAll(userPlanifications));

    }

    @PutMapping(baseUrl)
    public HttpEntity<List<UserPlanification>> editUserPlanification(@RequestBody List<UserPlanification> userPlanifications){
        return ResponseEntity.ok(userPlanificationRepository.saveAll(userPlanifications));

    }

}
