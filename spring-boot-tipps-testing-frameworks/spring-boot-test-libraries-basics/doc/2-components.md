# Components

Criar It Test em components:

* @Controller,
* @Service
* @Repository
* Como usar a anotação @MockBean 


# @SpringBootTest

Deve ser utilizada quando precisamos de usar funcionalidades do Spring Boot, ela vai procurar pela classe de configuração da aplicação e usa-la para subir o context da aplicação.

# @MockBean

A anotacao `@MockBean` vai adicionar os objectos falsos ao contexto do spring, esses objectos irão substituir qualquer objecto do mesmo tipo no contexto de aplicação e caso seja um atributo ele será injectado automaticamente.

