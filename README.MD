# Discord Impostor Game Bot

## Overview
This Discord bot allows users to play a simple "Who is the Impostor?" game directly in their Discord server. Inspired by the popular social deduction game, players try to identify the hidden impostor among them.

## Features
- Start a new game with 5 players
- Private role assignments
- Voting mechanism to identify the impostor
- Automatic game management

## Prerequisites
- Java Development Kit (JDK) 11 or higher
- Discord account
- Discord Developer Portal access

## Setup

### 1. Create a Discord Application
1. Go to the [Discord Developer Portal](https://discord.com/developers/applications)
2. Click "New Application"
3. Name your application
4. Go to the "Bot" section and create a bot
5. Copy the Bot Token

### 2. Configure the Bot
1. Create a `config.properties` file in your `resources` directory
2. Add your bot token:
   ```properties
   token=YOUR_BOT_TOKEN_HERE
   ```

### 3. Invite Bot to Server
1. In the Discord Developer Portal, go to OAuth2 > URL Generator
2. Select "bot" scope
3. Choose necessary bot permissions
4. Copy and open the generated URL to invite the bot

## Commands

### /start
- Starts a new game
- Requires 5 player mentions
- Example: `/start @Player1 @Player2 @Player3 @Player4 @Player5`

### /vote
- Initiates voting during an active game
- Players will receive a private message to select who they think is the impostor

## Gameplay
1. Use `/start` to begin a game with 5 players
2. The bot will privately message each player their role (impostor or crewmate)
3. Use `/vote` to start the voting phase
4. Players privately select who they believe is the impostor
5. Once all votes are in, the bot reveals the voting results and the actual impostor

## Dependencies
- JDA (Java Discord API)

## Limitations
- Exactly 5 players required
- One game at a time
- Game runs in the channel where it was started

## Troubleshooting
- Ensure bot has necessary permissions
- Check console for error messages
- Verify bot token is correct