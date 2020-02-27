# Instalar o Redis

Para instalar o Redis, é necessário entender o conceito de que, se vamos trabalhar com ele na nossa máquina, precisamos instalar o **Redis Server**. Caso fossemos apenas aceder ao Redis Server que está em outra maquina remota, precisaríamos apenas instalar o **Redis CLI**, que é o cliente do Redis que permite acessos ao Redis Server.

Posto isso, como vamos executar o Redis localmente, na nossa máquina, e fazer acessos a ela pelo cliente, temos que instalar o Redis Server. Normalmente, ao instalar o Redis Server, já teremos tambem com ele o Redis CLI.

Abaixo vamos descrever os processos de instalação do Redis Server nos SOs **Ubuntu**, **Windows** e **MacOS**, respectivamente.

## Ubuntu

1. Primeiro execute o comando abaixo para atualizar os repositórios:

    ```shell script
    sudo apt-get update
    sudo apt-get install redis-server
    ```

   - OBS: Se não encontrar o redis-server, adicione o seguinte repository abaixo e tente instalar novamente.

        ```shell script
        sudo add-apt-repository universe
        ```

2. Depois de instalar, execute o seguinte comando:
    ```shell script
    sudo service redis status
    ```
    - Esse comando serve para mostrar o status do serviço do Redis. 
    - Devemos receber uma série de informações, após executar esse comando, e detre as informações, devemos ver algo como "active (running)", o que significa que o serviço do Redis já se encontra em execução.

3. Então, após isso, basta executar o seguinte comando para aceder ao cliente do Redis:
    ``` shell script
    redis-cli
    ```


## Windows

Existem várias formas de instalar o Redis no Windows. 
Vamos enumerar a forma mais simples, para não entrarmos e níveis mais complexos de serviços do Windows.

1. Primeiro, use o [link](https://github.com/microsoftarchive/redis/releases/tag/win-3.0.504) para fazer download do ficheiro zip Redis-64-3.0.504.zip.

2. Depois do download completo, descompacte-o em uma pasta de sua preferência.

3. Agora devemos executar o ficheiro `redis-server` para que o Redis Server seja iniciado. Na primeira vez, será pedido permissões de administrador para executar este ficheiro, somos obrigados a concedê-la se pretendermos iniciar o ficheiro com sucesso.

4. Após isso, será aberta uma janela do prompt de comando (CMD), a indicar que o serviço do Redis Server está em execução.

5. Já com o Redis Server em execução, é necessário agora executar o Redis CLI para acedermos ao Redis Server. Para fazer isso, basta executar o ficheiro redis-cli na pasta que descompactamos anteriormente.

OBS: Sempre que for necessáro executar o Redis, é necessáro executar estes dois ficheiros. Se iniciarmos o ficheiro redis-cli sem iniciar o ficheiro redis-server, nada irá acontecer, pois o Redis CLI não irá consguir conectar-se com o serviço.

## Mac

1. No Mac o processo é muito simples. Basta executar o comando abaixo para actualizar os repositórios:

    ```shell script
    brew update
    ```

2. Depois executamos o comando abaixo para instalar o Redis:
    ```shell script
    brew install redis
    ```

3. Depois do Redis instalado, é necessário iniciar o serviço do Redis assim:
    ```shell script
    brew services start redis
    ```

4. Para entrar no Redis CLI, basta executar o seguinte comando:
    ```shell script
    redis-cli
    ```

## Testar Redis (independentemente do SO)

1. Para testar se a comunicação entre o Redis CLI e o Redis Server está a funcionar corretamente. podemos executar o comando abaixo, já dentro do Redis CLI:

    ```
    ping
    ```

2. Como resposta, devemos receber a palavra "PONG". Se receber esta palavra, significa que o Redis CLI está a comunicar corretamente com o Redis Server.