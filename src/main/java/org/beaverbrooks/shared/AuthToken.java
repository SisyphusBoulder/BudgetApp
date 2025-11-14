package org.beaverbrooks.shared;

import java.util.UUID;

public record AuthToken (UUID UserId, boolean IsAuthed) {}