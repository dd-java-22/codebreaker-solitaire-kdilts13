# GameSummary CRUD Inventory

## Create

- Create an instance after a game is started successfully, when the completed Game instance is returned from the web service.

## Read

- Retrieve all summaries for games that are not yet solved, ordered by the last date-time that a guess was submitted—or by the started date-time for the game, if no guesses have yet been submitted.


- Retrieve a specific summary by its local id or external game key.


- Retrieve the most recently played unsolved game summary so the user can resume the game quickly.


- Retrieve all solved summaries for a completed games stats screen.


- Retrieve all summaries.


- Retrieve summaries filtered by code length and / or character pool settings.

## Update

- Update a summary after each submitted guess to store new guess count, match counts, last-played date-time, and solved status.


- Update the last played time of a summary after a game is resumed.


- Update a stored summary if newer information is returned from the web service.

## Delete

- Delete a summary automatically if a user tries to load a game that is expired.


- Delete a specific summary or multiple specific summaries that the user chooses.


- Delete all summaries.


- Delete all solved summaries and / or summaries older than a specific cutoff as a cleanup action.