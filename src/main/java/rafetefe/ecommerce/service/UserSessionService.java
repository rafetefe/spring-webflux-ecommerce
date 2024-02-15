package rafetefe.ecommerce.service;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("singleton") //this is already the default value but wanted to try anyway.
public class UserSessionService {

    //Mock User Session Id
    public final Integer userId = 2024;

}
