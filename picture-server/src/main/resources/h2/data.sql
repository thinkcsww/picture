insert into oauth_client_details(client_id, resource_ids,client_secret,scope,authorized_grant_types,web_server_redirect_uri,authorities,access_token_validity,refresh_token_validity,additional_information,autoapprove)
values(
          'foo',
          null,
          '$2a$10$qv5L2BuqCa32y6DH2rR8aO0USoqLlicL3tcHdUpuk.bfXZB25NG2q',
          'read,write',
          'authorization_code,refresh_token',
          'http://localhost:8080/oauth2/callback',
          'ROLE_USER',
          36000,
          50000,
          null,
          null
      );

