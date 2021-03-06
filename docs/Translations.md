Translations
============

The translations module can be used by specifying the `TranslationModule` when setting up the initial modules.

This allows you to inject the service `Translate` which can be used to translate strings based on locales.

*The non-player commandsenders will default to 'en' as their locale unless the ConfigurationModule is also loaded which
adds a locales.yml to the plugin which allows setting the locale per sender type*

```
@Inject
public void setTranslate(Translate translate)
{
//store the translate object for use
}
```

Setting up translations
-----------------------

You will need a folder called 'translations' in your plugin JAR which contains files of the naming scheme `lang_CODE.yml`
where CODE is the language code (en_US by default). You *MUST* provide a file called lang.yml which is the default values
if no codes match.

Translate offers the following methods

### translate(String key, Locale locale, Object... params) and translate(String key, CommandSender sender, Object... params)

This offers a way to translate a key into the specified locale or the locale of the specified CommandSender.

`params` is the list of objects to pass into String.format() which is applied to the value of the key.

If the key is not found then the method will return the key itself.

### sendMessage(String key, CommandSender sender, Object... params)

Utility method that translates the key with translate() and sends the result to the sender

### broadcastMessage(String key, Object... params)

Utility method that sends a message to all online players in their locale

### broadcastMessageForPermission(String permission, String key, Object... params)

Same as broadcastMessage(String, Object...) but only sends  to players with the given permission

Locales
=======

You can get the locale for a particular commandsender by injecting a LocaleProvider object which allows the use of the
method `localeForCommandSender(CommandSender)`.


### localeForCommandSender(CommandSender sender)

Returns the stored Locale for the given CommandSender. If it is a player it is pulled from the NMS Player object which
is set from the client itself. If it is any other kind of sender it will be set to the locale from the config file.

*The non-player commandsenders will default to 'en' as their locale unless the ConfigurationModule is also loaded which
adds a locales.yml to the plugin which allows setting the locale per sender type*