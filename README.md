# systagramgae
Serviço restful do systagram

Recomendações para configuração do Bucket e banco Mysql do Google Cloud. 

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

Outra opção é passar o arquivo direto no código. Veja exemplo da appspring

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
