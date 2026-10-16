# graveplan
A tool converting exports of a Graveyard management programm into static html pages which show the "content" of graves in a table like plan.

## Website (www.friedhof-pattensen.de)
A run generates the complete web space content in `<outputDir>/website/`:

```
.htaccess                      redirect of / to the cemetery page of the parish
robots.txt
plan/.htaccess                 single Basic Auth login, dispatches by user to the current due date
plan/index.html                fallback landing page
plan/internal/.htaccess        internal users only
plan/internal/<yyyyMMdd>/      internal plan (all data)
plan/public/.htaccess          internal and public users
plan/public/<yyyyMMdd>/        public plan (no personal data)
```

The configuration is read from `src/main/resources/website.properties`. User names and the server path of the `.htpasswd` are put into `src/main/resources/website.local.properties` (git-ignored), which overrides the values of `website.properties`:

```
authUserFile=/absolute/server/path/.htpasswd
users.intern=user1,user2
users.public=user3
```

Passwords are maintained manually with `htpasswd -B .htpasswd <user>`.
Upload for a new due date: `plan/internal/<ts>/`, `plan/public/<ts>/`, `plan/.htaccess` and `plan/index.html`.
Older due date directories can stay on the server; switching back only requires the corresponding `plan/.htaccess` and `plan/index.html`.
