# AdventureCalendar
A plugin to replicate that childhood feeling of going through an advent calendar!

## About

After seeing how few advent calendar plugins there are (specifically a bloated paid one I shall not name), I decided to create my own.

This plugin provides an advent calendar, and most importantly the framework for one. If you don't like the menu, it's super easy to use commands and placeholders to create your own.

## Usage

This plugin requires [Spigot](https://www.spigotmc.org/resources/adventurecalendar.97849/) (or a fork like Paper) and [RedLib](https://github.com/Redempt/RedLib/releases).

To add items and commands to presents, enter the editor with `/acal editor`.

Players will use `/acal` to open the GUI (you must grant the `acal.menu` permission!)

## Commands
`/adventurecalendar` (`/acal`):
* `reload` - reload the config
* `edit` - enter the present editor
* `claim [player] [day] [-f]` - claim a present (all arguments are optional, day will default to current day, use `-f` to bypass all checks and grant rewards for that day regardless)
* `reset [day/all] [player/everyone]` - reset claim status of a present, or all presents, for somebody, or everybody

## Permissions
* `acal.menu` - required to open the menu with `/ac`
* `acal.reload` - `/acal reload`
* `acal.edit` - `/acal edit`
* `acal.claim` - `/acal claim` (command only - will not change ability to claim via the GUI)
* `acal.reset` - `/acal reset`
* `acal.bypass` - open the GUI outside of the intended month
* `acal.claim.others` - ability to use `/acal claim` on other players
* `acal.claim.force` - ability to use `-f` in `/acal claim` - dangerous permission, allows for unlimited claiming

## Config
* `month` - the month during which to activate the calendar
* `first-day`, `last-day` - days between which presents will be avaliable
* `sql` - optional, used to fill out info for a MySQL database
* `items` - info for different display items in the `/ac` menu
  * Supports Head Database, use the format `hdb-(id)` for heads as materials
* `use-claimed-item-from-day-automatically` - displays item from the list of items for a present
* `gui.command-alias` - empty will open the default menu, otherwise `/acal` will be mapped to this command

## Placeholders
This plugin supports PlaceholderAPI by default. There are a couple of placeholders you can use to interact with other plugins, especially GUI menus:
* `%adventurecalendar_next%` - returns the day of the upcoming present
* `%adventurecalendar_timeuntil_next%` - returns the time until the next present
* `%adventurecalendar_timeuntil_(day)%` - returns time until a specific present
* `%adventurecalendar_claimed_total%` - returns the total number of presents claimed
* `%adventurecalendar_claimed_(day)%` - returns true or false based on whether the present that day has been claimed
