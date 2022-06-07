insert into oauth_client_details(client_id, resource_ids,client_secret,scope,authorized_grant_types,web_server_redirect_uri,authorities,access_token_validity,refresh_token_validity,additional_information,autoapprove)
values(
          'applory',
          null,
          '$2a$10$Qqt8zEo49AFNZ2DB7Jmt7ep3dNhVk2bXu7hk1oN8YAXvvsleSxCT2',
          'read,write',
          'password,refresh_token',
          'http://localhost:8080/oauth2/callback',
          'ROLE_USER',
          86400,
          2592000,
          null,
          null
      );

