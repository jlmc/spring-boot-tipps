package io.costax.idoit.tasks.control;

import io.costax.idoit.tasks.entity.Task;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.Matchers.notNullValue;

/**
 * <h1>@RunWith</h1>
 * A anotação @RunWith recebe como parametro uma extenssão de <code>org.junit.runner.Runner</code> vai dizer ao Junit usar a classe passada como parâmetro para executar os testes.
 *
 * <h1>SpringRunner</h1>
 * A classe SpringRunner vai iniciar o contexto de testes do spring e executar os testes.
 * O contexto de testes do spring dá suporte de forma generica para os testes unitarios e de integração, ou seja,
 * podemos usar outros frameworks de testes para alem do Junit
 *
 * <h1>@DataJpaTest</h1>
 * <p>
 * Contem configurações que permitem:
 *
 * <ul>
 *
 *
 * <li>utilização do <b>Spring Data JPA Repository</b> sem precisar
 * levantar a totalidade do contexto do spring</li>
 *
 * <li>Configura uma database embutida para ser usado em momoria durante os testes</li>
 *
 * </ul>
 * <p>
 * <p>
 * A DataJPATest procura por classes marcadas com a anotação @Entity, assim podemos usar Dependency injection
 * no test.
 * <p>
 * Como estamos a usar a anotação @DataJPATest por default as configurações do datasource foram subescritas
 */
@RunWith(SpringRunner.class)
@DataJpaTest
public class TaskDefaultRepositoryIT {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Autowired
    private TaskRepository taskRepository;

    private Task doc;

    @Before
    public void before() {
        doc = new Task();
        doc.setTitle("buy christmas gifts");
    }

    @Test
    public void shouldGetTaskById() {
        final Task task1 = new Task("Pay PES 2018");
        Long id = taskRepository.save(task1).getId();

        Task task = taskRepository.findById(id).orElse(null);

        Assert.assertThat(task, notNullValue());
        Assert.assertThat(task.getTitle(), Matchers.is("Pay PES 2018"));
    }

    @Test
    public void shouldNotCreateTaskWithBlankNames() {
        expectedException.expect(javax.validation.ConstraintViolationException.class);

        var t = new Task();
        t.setTitle("");

        Task task = taskRepository.save(t);

        Assert.assertThat(task, notNullValue());
    }

    @Test
    public void shouldCreateTaskWithNotBlankNames() {
        Task task = taskRepository.save(doc);

        Assert.assertThat(task, notNullValue());
    }


}