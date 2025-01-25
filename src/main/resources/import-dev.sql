create table "opt_user"
(
    id              integer not null,
    username        text    not null,
    password        text    not null,
    role            text    not null,
    uniqueId        uuid    not null,
    googleUserId    text    null,
    accessToken     text    null,
    refreshToken    text    null,
    tokenExpiry     bigint  not null
);

INSERT INTO "opt_user"
(id, username, password, role, uniqueId, googleUserId, accessToken, refreshToken, tokenExpiry)
VALUES
(1, 'frederic.courcier@gmail.com', 'pericard42', 'user', 'ABCDEFGH-1234-5678-90IJ_KLMNOPQRSTUV', null, null, null, 0),
(2, 'carole.courcier@gmail.com', 'pericard42', 'user', 'XYZDEFGH-1234-5678-90IJ_KLMNOPQRSTUV', null, null, null, 0);
