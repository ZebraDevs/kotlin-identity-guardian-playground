# Identity Guardian Sample Application

Demo Application demonstrating the integration of all the Identity Guardian's APIs.

## Features

- All APIs are automatically pre-granted through the AccessMgr APIs via the EMDK library
- Featured APIs:
  - `/v2/currentsession`
  - `/previoussession`
  - `/lockscreenstatus/state`
  - `/lockscreenaction/startauthentication`
  - `/lockscreenaction/authenticationstatus`
  - `/lockscreenaction/logout`
  - `/lockscreenaction/showmessage`
  - `/currentsession` (Deprecated)

## MSAL Integration

Identity Guardian offers SSO intgration for 3 main IdPs:
- Microsoft
- PingID
- Okta

Documented here:
[SSO Setup](https://techdocs.zebra.com/identityguardian/2-1/setup/#sso)

If you're using Microsoft as a way to seamlessly authenticate in your organization and you're looking for integrating it also with Identity Guardian, this sample app includes a basic integration for MSAL, meaning you'll be able to check the user session or even read information related to the user account.
Assuming you've already successfully integrated Identity Guardian with Microsoft Entra, you'll also need to create a new (similar) app configuration so you can use this app for debugging.
<br>
<br>
Once you have it set up, you'll need to make these changes to the code:

- Create msal.properties under `/app/`

The file should include these params which will be populated with your host and path values being used to launch the BrowserTabActivity for sign-in operations:
<br>
```groovy
ENTRA_HOST
ENTRA_REDIRECT_PATH
```

- Add your json configuration under `/app/src/main/res/raw/msal_auth_config.json`, it should be something similar like this:

```json
{
  "client_id": "Your Client Id",
  "redirect_uri": "Your Redirect URI",
  "account_mode": "SINGLE",
  "broker_redirect_uri_registered": true,
  "authorization_user_agent": "DEFAULT",
  "authorities": [
    {
      "type": "AAD",
      "audience": {
        "type": "AzureADMyOrg",
        "tenant_id": "Your Tenant ID"
      }
    }
  ]
}
```

If you've done everything correctly, the build should succeed and you should be able to authenticate in the sample application with the same Microsoft account being logged already under Identity Guardian.

## Blog Post

This demo is part of a blog post which has been released on the Zebra Developer Portal where I'm explaining step by step how to integrate these APIs inside a project.
If you're interested, feel free to head over [here](https://developer.zebra.com/blog/mastering-identity-guardians-apis).

## Disclaimer

Please be aware that this application is distributed as is without any guarantee of additional support or updates.

# License

[MIT](LICENSE.txt)