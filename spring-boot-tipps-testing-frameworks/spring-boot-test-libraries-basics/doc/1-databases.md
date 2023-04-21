# Data Base integration tests



## @RunWith

A anotação `@RunWith` recebe como parametro uma extenssão de `org.junit.runner.Runner` vai dizer ao Junit usar a classe passada como parâmetro para executar os testes.

## SpringRunner

A classe `SpringRunner` vai iniciar o contexto de testes do spring e executar os testes.
O contexto de testes do spring dá suporte de forma generica para os testes unitarios e de integração, ou seja, podemos usar outros frameworks de testes para alem do Junit.

## @DataJpaTest

Contem configurações que permitem:

* utilização do <b>Spring Data JPA Repository</b> sem precisar levantar a totalidade do contexto do spring
* Configura uma database embutida para ser usado em momoria durante os testes.
* A `DataJPATest` procura por classes marcadas com a anotação `@Entity`, assim podemos usar Dependency injection no test.
* Como estamos a usar a anotação `@DataJPATest` por default as configurações do datasource foram subescritas.




Podemos confirmar que realmente estamos a utilizar uma base de dados embutiva em memoria que é apenas dedicada aos tests atraves da saida do logger do test case.

```log
2018-12-25 19:45:28.521  INFO 676 --- [           main] i.c.i.tasks.control.TaskRepositoryIT     : Starting TaskRepositoryIT on atlas.local with PID 676 (started by costa in /Users/costa/Documents/junk/costax/spring-its/idoit)
2018-12-25 19:45:28.523  INFO 676 --- [           main] i.c.i.tasks.control.TaskRepositoryIT     : No active profile set, falling back to default profiles: default
2018-12-25 19:45:28.903  INFO 676 --- [           main] .s.d.r.c.RepositoryConfigurationDelegate : Bootstrapping Spring Data repositories in DEFAULT mode.
2018-12-25 19:45:28.963  INFO 676 --- [           main] .s.d.r.c.RepositoryConfigurationDelegate : Finished Spring Data repository scanning in 53ms. Found 1 repository interfaces.
2018-12-25 19:45:29.009  INFO 676 --- [           main] beddedDataSourceBeanFactoryPostProcessor : Replacing 'dataSource' DataSource bean with embedded version
2018-12-25 19:45:29.299  INFO 676 --- [           main] o.s.j.d.e.EmbeddedDatabaseFactory        : Starting embedded database: url='jdbc:h2:mem:98bcfc79-91f1-43cf-8b80-e08f1c9a71e7;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=false', username='sa'
2018-12-25 19:45:29.490  INFO 676 --- [           main] o.f.c.internal.license.VersionPrinter    : Flyway Community Edition 5.2.3 by Boxfuse
2018-12-25 19:45:29.501  INFO 676 --- [           main] o.f.c.internal.database.DatabaseFactory  : Database: jdbc:h2:mem:98bcfc79-91f1-43cf-8b80-e08f1c9a71e7 (H2 1.4)
2018-12-25 19:45:29.600  INFO 676 --- [           main] o.f.core.internal.command.DbValidate     : Successfully validated 1 migration (execution time 00:00.020s)
2018-12-25 19:45:29.618  INFO 676 --- [           main] o.f.c.i.s.JdbcTableSchemaHistory         : Creating Schema History table: "PUBLIC"."flyway_schema_history"
2018-12-25 19:45:29.646  INFO 676 --- [           main] o.f.core.internal.command.DbMigrate      : Current version of schema "PUBLIC": << Empty Schema >>
2018-12-25 19:45:29.647  INFO 676 --- [           main] o.f.core.internal.command.DbMigrate      : Migrating schema "PUBLIC" to version 1 - create task table
2018-12-25 19:45:29.671  INFO 676 --- [           main] o.f.core.internal.command.DbMigrate      : Successfully applied 1 migration to schema "PUBLIC" (execution time 00:00.060s)
2018-12-25 19:45:29.836  INFO 676 --- [           main] o.hibernate.jpa.internal.util.LogHelper  : HHH000204: Processing PersistenceUnitInfo [
	name: default
	...]

```

## Mas, e se quiser mos utilizar outros tipos de database?

Tambem é possivel executar os testes em outros de tipos de Base de dados Embeddable.

Para isso fazemos uso da anotação:

```
@AutoConfigureTestDatabase
```

Com a anotação `@AutoConfigureTestDatabase` podemos usar, o **H2**, o **Derby** ou o **HSQL**
para ser utilizado como base de dados de testes. Basta usar a propriedade **connection**, esta propriedade configura o **H2**, **Derby** ou **HSQL**.

A propriedade `connection` recebe um **EmbeddedDatabaseConnection** que é uma enum com 4 opções:

* NONE -  usa uma das DB que estejam disponiveis. (default) irá um dos restantes tipos de DBs que estejam disponiveis (em que a dependencia esteja no pom file), caso não exista qualquer Db disponivel uma exception será lançada
* H2 - Configura o H2
* DERBY - Configura o DERBY
* HSQL - Configura o HSQL


### EmbeddedDatabaseConnection

A propriedade `replace` define os tipos de datasource que serão substituidos, ele receve uma enum Replace com as seguintes opções:

* ANY - substitui qualquer datasource
* AUTO_CONFIGURED - substitui apenas os datasources configurados automaticamente
* NONE - Não substitui o datasource da aplicação.


# Mas como usamos um datasource pre-configurado.

O significa na prática colocar  `@AutoConfigureTestDatabase(replace=Replace.NONE)`, ele não irá substituir  nenhum dos databases configurados, ou seja, ele executará os testes em na database de produção (ou outra environment, dev ou QA por exemplo)

Não é que ela irá usar o DB de produção, ela irá usar o DB que estiver configurado para a aplicação se conectar.
Por exemplo, digamos que a aplicação esteja configurada para acessar uma DB mysql em QA.
Se apenas utilizarmos a anotação `@DataJpaTest`, essa configuração será substituida para que o teste seja executado usando o **DB**, **H2**, **Hsql** ou **Derby**.

Quando usamos  `@AutoConfigureTestDatabase(replace=Replace.NONE)` o spring vai primeiro procurar o ficheiro **application.properties**
na folder **src/test/resources**.
Caso ele encontre o ficheiro, ele vai usar a configuração definida nesse ficheiro.
Se ele não encontrar, então vai procurar o DB da aplicação, ou seja o banco de QA, para executar os testes.

Quando usamos `@AutoConfigureTestDatabase(replace=Replace.NONE)`, o spring segue uma serie de passos que para tentar configurar uma base de dados para o teste.  
Primeiro ele vai procurar o ficheiro application.properties na directoria `src/test/resources`.
- Caso ele encontre, ele vai usar a configuração definida no ficheiro.
- Caso ele encontre o ficheiro, mas o ficheiro não tenha nenhuma configuração de DB definida, ele vai usar o `h2`, `hsql` ou `derby`.
- Caso ele não encontre o ficheiro, ele vai procurar a configuração do DB da aplicação
- E caso ele não encontre a configuração do DB da aplicação ele vai ele vai usar o `h2`, `hsql` ou `derby`.


* Como fazemos para nos testes o spring usar o `application.properties` de  `src/tests/resources`, que seria as configurações para um DB de testes  sem ser os já embutidos no spring, em vez de sr/main/resources que na  teoria seria o DB de produção?*


1. criar a estrutura `src/test/resources`,
2. criar o ficheiro application.properties e nele definir as configuração para o DB de dados que quisermos.
3. Após isso, quando usarmos a anotação `@DataJpaTest` com `@AutoConfigureTestDatabase(replace=Replace.NONE)`, ou, em alternativa,  a anotação `@SpringBootTest`, o spring vai procurar primeiro as configurações nesse fichero
