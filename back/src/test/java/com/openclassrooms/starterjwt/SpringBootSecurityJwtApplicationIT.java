package com.openclassrooms.starterjwt;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.openclassrooms.starterjwt.controllers.AuthController;
import com.openclassrooms.starterjwt.controllers.SessionController;
import com.openclassrooms.starterjwt.controllers.TeacherController;
import com.openclassrooms.starterjwt.controllers.UserController;

@SpringBootTest
public class SpringBootSecurityJwtApplicationIT {

  @Autowired
  private AuthController authController;

  @Autowired
  private UserController userController;

  @Autowired
  private TeacherController teacherController;

  @Autowired
  private SessionController sessionController;


  @Test
  void contextLoads() {
    assertNotNull(authController);
    assertNotNull(userController);
    assertNotNull(teacherController);
	  assertNotNull(sessionController);
  }

  @Test
  public void applicationContextTest() {
    SpringBootSecurityJwtApplication.main(new String[] {});
  }

}
