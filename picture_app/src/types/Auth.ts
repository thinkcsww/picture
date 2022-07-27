export namespace Auth {
  export interface MyOAuth2Token {
    access_token: string,
    token_type: string,
    refresh_token: string,
    expires_in: number,
    scope: string,
    jti: string,
  }

  export class LoginDto {
    public username: string;
    public token: string;

    constructor(username: string, token: string) {
      this.username = username;
      this.token = token;
    }
  }
}
