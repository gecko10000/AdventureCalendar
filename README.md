# AdventureCalendar
Minecraft plugin that adds an advent calendar for the holidays.

## About

After seeing how few advent calendar plugins there are (specifically a bloated paid one I shall not name), I decided to create my own.

This plugin provides an advent calendar, and most importantly the framework for one. If you don't like the menu, it's super easy to use commands and placeholders to create your own.

## Usage

This plugin requires [Spigot](https://www.spigotmc.org/wiki/buildtools/) (or a fork like Paper) and [RedLib](https://github.com/Redempt/RedLib/releases).

To add items and commands to presents, enter the editor with `/ac editor`.

Players will use `/ac` to open the GUI (you must grant the `ac.menu` permission!)

## Commands

`/adventurecalendar` (`/ac`):
* `reload` - reload the config
* `edit` - enter the present editor
* `claim [player] [day] [-f]` - claim a present (all arguments are optional, day will default to current day, use `-f` to bypass all checks and grant rewards for that day regardless)
* `reset [day/all] [player/everyone]` - reset claim status of a present, or all presents, for somebody, or everybody

## Permissions
* `ac.menu` - required to open the menu with `/ac`
* `ac.reload` - `/ac reload`
* `ac.edit` - `/ac edit`
* `ac.claim` - `/ac claim` (command only - will not change ability to claim via the GUI)
* `ac.reset` - `/ac reset`
* `ac.bypass` - open the GUI outside of the intended month
* `ac.claim.force` - ability to use `-f` in `/ac claim` - dangerous permission, allows for unlimited claiming
* `ac.claim.others` - ability to use `/ac claim` on other players

## Config
* `month` - the month during which to activate the calendar
* `first-day`, `last-day` - days between which presents will be avaliable
* `sql` - optional, used to fill out info for a MySQL database
* `items` - info for different display items in the `/ac` menu
* `use-claimed-item-from-day-automatically` - displays item from the list of items for a present
* `gui.command-alias` - empty will open the default menu, otherwise `/ac` will be mapped to this command
