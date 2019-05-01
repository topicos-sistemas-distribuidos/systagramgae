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
