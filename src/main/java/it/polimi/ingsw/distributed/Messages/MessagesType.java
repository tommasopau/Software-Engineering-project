package it.polimi.ingsw.distributed.Messages;

/**
 * Enum of the different types of messages used for RMI connection
 */
public enum MessagesType {
    START,
    NICKNAME_CORRECT,
    NICKNAME_TOO_LONG,
    NICKNAME_TAKEN,
    NICKNAME_FULL_GAME,
    NICKNAME_NOT_VALID_CHARACTERS,
    NUMBER_OF_PLAYERS_NOT_VALID,
    NUMBER_OF_PLAYERS_NEEDED,
    NUMBER_OF_PLAYERS_NOT_NEEDED,
    CORRECT_NUMBER_OF_PLAYERS,
    GAME_START,
    GAME_HAS_NOT_FINISHED,
    GAME_FINISHED,
    PLAYER_ON_TURN,
    NOT_PLAYER_ON_TURN,
    NOT_ENOUGH_SPACE_TAKE,
    NUMBER_OF_TILES_WANTED_NOT_VALID,
    NOT_TAKEABLE_TILE,
    NOT_VALID_CONFIGURATION_TAKE,
    DUPLICATE_TAKE_TILE,
    NOT_ENOUGH_TAKEABLE_TILES,
    CORRECT_TAKE,
    NOT_VALID_ORDER_VALUES,
    DUPLICATE_ORDER_VALUES,
    CORRECT_ORDER_VALUES,
    NOT_CORRECT_VALUE_PLACE,
    NOT_ENOUGH_SPACE_IN_COLUMN,
    CORRECT_VALUE_PLACE,
    FULL_BOOKSHELF,
    NOT_FULL_BOOKSHELF,
    NEW_GAME_PERSISTENCE,
    GAME_ALREADY_CREATED_PERSISTENCE,
    NICKNAME_NOT_FOUND_PERSISTENCE
}
