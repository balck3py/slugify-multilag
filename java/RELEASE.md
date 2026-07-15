# Maven Central Release

The `release` Maven profile attaches source and Javadoc archives, signs the
release artifacts, and configures the Central Publisher Portal Maven plugin.
It deliberately does not auto-publish.

## One-time account setup

1. Register at [Central Publisher Portal](https://central.sonatype.com/) and
   verify ownership of the `io.github.balck3py` namespace.
2. Generate a Central user token and add it to `~/.m2/settings.xml`:

   ```xml
   <settings>
     <servers>
       <server>
         <id>central</id>
         <username>YOUR_TOKEN_USERNAME</username>
         <password>YOUR_TOKEN_PASSWORD</password>
       </server>
     </servers>
   </settings>
   ```

3. Create or import a personal GPG key, protect it with a passphrase, and
   publish its public key. Do not commit keys, tokens, or passphrases.

## Release commands

```powershell
cd java
mvn verify
mvn -Prelease deploy
```

The first command creates the main, sources, and Javadoc JARs. The second
signs them and submits a deployment for validation. The Portal remains in
control of final publication because `autoPublish` is disabled.
