# SystagramRest
Serviço restful do systagram (https://github.com/topicos-sistemas-distribuidos/systagram) para prover os recursos da aplicação para clientes móveis e web. 

O Google API Engine [1] é uma plataforma do Google Cloud [2] para desenvolver e hospedar aplicações web na infraestrutura do Google. Esta API do Google é uma tecnologia que usa o modelo de desenvolvimento de aplicações para o ambiente de nuvem de Plataforma como Serviço que permite a criação de aplicações totalmente gerenciadas e sem servidores (Servless). 

A linguagem escolhida para criar o SystagramRest foi o Java integrando o Google Cloud SDK [3] com a IDE eclipse e um projeto criado no Google Cloud. 

Uma das vantagens da implantação da aplicação no Google API Engine é o fato de fazer o mínimo de configuração de infraestrutura e uma vez a aplicação implantada em produção ela já vem configurada com o fator de segurança SSL (htts). Além disso, o mesmo oferece uma boa opção de monitoramento, geração de registros, diagnóstico da aplicação, balanceamento de carga e requisições dos clientes. 

Os seguintes passos foram seguidos para garantir a construção da Aplicação SystagramRest prover os recursos que serão consumidos por clientes web e mobile no padrão Restful. 

1. Instalar o Google Cloud SDK no Eclipse. 

2. A linguagem escolhida para a contrução da aplicação foi o Java devido a maior familiaridade dos autores da aplicação. 

3. O Google API Engine exige que seja respeitado o padrão de desenvolvimento provido pela plataforma. Com isso, foi necessário fazer um estudo de frameworks suportados como por exemplo o framework Spring Cloud [4] e o framework Restlet [5]. Como o modelo arquitetural escolhido foi o modelo Restful [6], foi escolhido o framework Restlet para a construção do servidor de recursos da aplicação. 

4. O modelo de aplicação do Google API Engine escolhido foi o "App Engine Standard" que usa como referência o padrão Servlet 3.1 [7] e tem como Container Java o Jetty Eclipse [8]. Com isso, durante a construção da aplicação foi necessário seguir os padrões e limitações adotados por essas tecnologias. 

Obs: Os aplicativos no padrão "App Engine Standard" são executados em um ambiente seguro e em área restrita, permitindo que o ambiente padrão do App Engine distribua solicitações em vários servidores e dimensionando servidores para atender às demandas de tráfego. Seu aplicativo é executado dentro de seu próprio ambiente seguro e confiável, independente do hardware, do sistema operacional ou da localização física do servidor.

5. O projeto Java foi criado baseado no modelo de Projeto Java Web Aplication no padrão Maven [9] para controle das dependências. 

6. Uma vez criado o projeto o mesmo foi transformado para o padrão "App Engine Standard".

7. Para garantir o funcionamento da aplicação foi necessário importar as dependências e fazer a mesma apontar para o Jetty. Além de importar as dependências do Google Cloud (com.google.cloud) e Google API Engine (com.google.appengine) para garantir o uso dos recursos PaaS do Google. 

8. O armazenamento dos arquivos de fotos gerenciados pela aplicação é feito no serviço Google Cloud Store [10]. Para isso, foi necessário criar um componente que se comunicasse com tal serviço. Com isso, foi necessário criar um esquema de controle de autenticação e envio de arquivos para o Google Cloud Store. 

9. O armazenamento das informações gerenciadas pela aplicação é feito no serviço Google Cloud SQL [11] usando o banco MySql [12] e o conjunto de componentes providos pelo JPA [13] para controle de comunicação com o banco usando os padrão ORM (Object Relational Mapping) via Hibernate [14]. 


Recomendações técnicas de configuração de Bucket e Banco de Dados
---

1. Criação do Bucket 
meu-bucket-files

2. Adicionar a dependência do cloud storage no nosso projeto. Para isso, vá até o pom.xml

<dependency>
    <groupId>com.google.cloud</groupId>
    <artifactId>google-cloud-storage</artifactId>
    <version>1.14.0</version>
</dependency>

3. Criar uma classe para salvar os arquivos no Bucket criado. 

4. Criar uma chave de acesso da aplicação para o projeto do Google Cloud. 

Devemos configurar a chave de acesso que será utilizada pela nossa aplicação para que possamos assim salvar as imagens nesse Bucket que criamos.

Obs: Nós precisamos de uma chave com as credenciais necessárias que será utilizada por nossa aplicação para enviar as imagens.

5. Acesso da API

Criar Google Service Key

permitir-acesso-servicos-google-cloud

Feito isso, devemos dar o nome para essa credencial que estamos configurando, podemos dar o nome que desejarmos, por exemplo permitir-acesso-servicos-google-cloud e na permissão Role escolha que tal conta terá a possibilidade de realizar edição no projeto, sendo assim possível tanto de realizar a leitura como a escrita de dados. Escolha a opção Editor para essa conta, para finalizar, faça o download da chave no formato JSON para que nossa aplicação que ainda está no ambiente local de desenvolvimento salve as imagens no Bucket.

A criação do objeto Storage na classe FileSaver vai procurar essas credencias de acesso presentes no arquivo JSON através da variável de ambiente GOOGLE_APPLICATION_CREDENTIALS, precisamos configurar na nossa máquina essa variável de ambiente:

Para configurar a variável de ambiente no MAC:  

touch ~/.bash_profile
open -a TextEdit.app ~/.bash_profile
export GOOGLE_APPLICATION_CREDENTIALS=[Local da chave]

Outra opção é passar o arquivo direto no código. 

6. Criar o banco de dados no Google Cloud

a) Criar o banco

b) Liberar o acesso de IP público. Para isso, clique em Show configuration options e na parte de redes autorizadas Authorize Networks coloque o endereço 0.0.0.0/0

Integração com o Google API Engine (GAE)
---
1. Criar um novo projeto para construir a aplicação usando o Google API Engine. 
* Nome do projeto
systagramgae

* Escolha a linguagem java

2. Setar o projeto via Gcloud.

$ gcloud config set project systagramgae

3. Garantir que os componentes api-java estejam instalados.

$ gcloud components install app-engine-java

4. Instalar o plugin do google cloud sdk no Eclipse.

5. Abrir o projeto java criado e converter para Api Engine Padrão.

Obs: caso ocorra algum erro de incompatibilidade de componentes
https://github.com/GoogleCloudPlatform/google-cloud-eclipse/issues/2285

6. Criar a instancia do banco.

a) Dados
banco-systagram

b) Criar o banco da aplicação 
Entre na instância do banco e crie o banco (dbsystagram) da aplicação. 

7. Vá até a classe de configuração de acesso ao banco e atualize o IP da nova instância criada.

8. Vá até a lista de buckets do projeto e escolha um bucket já criado no projeto. 
obs: faça o ajuste para permitir escrita e leitura no bucket pela aplicação. 

Crie uma chave de acesso ao projeto e salve temporariamente na pasta principal do repositório de código do projeto.

Painel -> Api e Serviços -> Credenciais -> Criar nova credencial -> Chave de conta de serviço -> Acesso ao projeto

9. Libere a porta 8080 no firewall do projeto systagramgae.

Painel -> VPC Network -> Firewall Rules -> Create Firewall Rule

O primeiro passo que devemos fazer é dar um nome para essa nova regra que queremos configurar no Firewall, por exemplo liberando-acesso-porta-8080, depois certifique-se que essa regra irá analisar o tráfego que irá entrar no Google Cloud vindo da internet (Ingress) e que iremos permitir esse tráfego (Allow). Posteriormente, coloque que qualquer endereço IP terá permissão de acessar a porta 8080 da instância (0.0.0.0/0)

Target: All instances in the internet
Source filter: IP ranges
Source IP ranges: 0.0.0.0/0

Specified protocols and ports -> tcp -> 8080

References
---

[1] Google API Engine. Available at https://cloud.google.com/appengine/

[2] Google Cloud. Available at https://console.cloud.google.com

[3] Google Cloud SDK. Available at https://cloud.google.com/sdk

[4] Framework Spring Cloud. Available at https://spring.io/projects/spring-cloud

[5] Framework Restlet. Available at https://restlet.com/open-source/

[6] Restful. I, Roy Thomas Fielding. Available at https://www.ics.uci.edu/~fielding/pubs/dissertation/fielding_dissertation.pdf

[7] Servlets. Available at https://www.oracle.com/technetwork/java/javaee/servlet/index.html

[8] Jetty. Available at https://www.eclipse.org/jetty/

[9] Maven. Management of Builds and Dependencies. Available at https://maven.apache.org

[10] Google Storage. Available at https://cloud.google.com/storage/

[11] Google Cloud SQL. Available at https://cloud.google.com/sql

[12] Mysql 5. Database Management System. Available at https://dev.mysql.com/downloads/mysql

[13] JPA. Available at https://docs.oracle.com/javaee/7/tutorial/overview007.htm

[14] Hibernate. Available at https://hibernate.org

Questions, suggestions or any kind of criticism contact us by email armando@ufpi.edu.br
