

# bot-echo

This is my first approach to Telegram Bots. Is the *baseline* to made another bots. Also is used to solve a problem in a non-profit organization communication channel.

## Description

Is a springframework app that runs on a server and pull messages from telegram in order to send via email to a predefined address.

This bot can be used by as many users as you want. The user begins a chat with the bot and any message that user send, is re-send to configured email address.

Use the [Telegram Spring boot starter](https://github.com/xabgesagtx/telegram-spring-boot-starter) from [https://github.com/xabgesagtx](https://github.com/xabgesagtx).

## Configuration

In order to use the app, you need to configure two elements:

* email
* telegram bot

Both configuration are sensible and should to be hidden in repository.

The `application.yml` uses maven resource tokens in order to replace them on compilation phase.

The values, typically, are on `settings.xml` or in a external properties file.

### Email

The replacement tokens for email are:

| Token                | Description                        |
| -------------------- | ---------------------------------- |
| `bot.email.host`     | The email server host name         |
| `bot.email.username` | The email username to open session |
| `bot.email.password` | The password                       |

### Telegram bot

The replacement tokens for telegram bot are:

| Token                | Description                        |
| -------------------- | ---------------------------------- |
| `bot.tg.token` | The token generated for bot |
| `bot.tg.username` | The userename of bot |
| `bot.tg.email.to` | The *to* address when sending the email (typically any administrator or master) |
| `bot.tg.email.from` | The *from* address when sending the email |
| `bot.tg.missatges.benvinguda` | A *welcome* message for the user when starts the bot |
| `bot.tg.missatges.resposta` | A *response* message for the user after receive their text message via telegram bot chat |

## Use

### Test from command line

You can run as any other spring-boot app and use them.

### Use as a linux systemd service

At `src/assembly` there are some template files to install the bot as a linux systemd service:

* `telegram-bot-echo.sh` is a bash script to start the bot
* `telegram-bot-echo.service` is a systemd service descriptor for the bot

Should to copy the two files and the build jar into a specific directory of your election.

Update the files to indicate the correct path to your installation folder and the JAVA HOME.

If you want to use external configuration, create a subdir named `config` and place in it a application.yaml file with your specific configuration (you can copy and paste contents of `src/main/resources/application.yaml` and update with configuration).

For configuration take effect, should to update the file `telegram-bot-echo.sh` and comment and uncomment the two lines that set the `CP` environment variable.

These templates use a specific user and group for execution:

* user: bots
* group: bots

You should to create them and change ownership of installation directory to these user and group.

When installation is completed, can test simply running `telegram-bot-echo.sh` from command line.

If all is ok, you can install the service:

```bash
sudo cp telegram-bot-echo.service /etc/systemd/system
sudo systemctl daemon reload
```

And then operate:

**Start:**

```bash
sudo systemctl start telegram-bot-echo
```

**Enable on boot:**

```bash
sudo systemctl enable telegram-bot-echo
```

**Check status:**

```bash
sudo systemctl status telegram-bot-echo
```

