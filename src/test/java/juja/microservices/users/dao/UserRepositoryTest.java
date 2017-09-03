package juja.microservices.users.dao;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import juja.microservices.users.dao.users.domain.User;

import juja.microservices.users.dao.users.repository.UserRepository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

/**
 * @author Vadim Dyachenko
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class,
        DbUnitTestExecutionListener.class})
@EnableTransactionManagement
@DbUnitConfiguration(databaseConnection = {"usersConnection", "crmConnection"})
@DatabaseSetup(value = "/datasets/usersData.xml")
public class UserRepositoryTest {

    @Inject
    private UserRepository repository;

    @Test
    public void testFindAll() throws Exception {
        List<User> users = repository.findAll();
        assertEquals(2, users.size());
        assertEquals("Alex", users.get(0).getFirstName());
        assertEquals("Max", users.get(1).getFirstName());
    }

    @Test
    public void testFindBySlack() throws Exception {
        User user = repository.findOneBySlack("alex.batman");
        assertEquals("Batman Alex", user.getFullName());
        assertEquals("alex.batman", user.getSlack());
    }

    @Test
    public void testFindByUuid() throws Exception {
        User user = repository.findOneByUuid(new UUID(1L, 3L));
        assertEquals("Superman Max", user.getFullName());
    }

    @Test
    @Transactional
    @ExpectedDatabase(value = "/datasets/usersDataAfterUpdate.xml")
    public void testUpdateUsersDatabaseFromCRM() throws Exception {
        List<User> users = new ArrayList<>();
        users.add(new User(UUID.fromString("00000000-0000-0001-0000-000000000003"), "Max", "Superman",
                "superman@ab.com", "max.superman@gmail.com", "Max", "Max", 200L));
        users.add(new User(UUID.fromString("00000000-0000-0001-0000-000000000004"), "Sergey", "Spiderman",
                "sergey.spiderman@ab.com", "sergey.spiderman@gmail.com", "sergey.spiderman", "Sergey", 250L));

        repository.save(users);
        repository.flush();
    }
}