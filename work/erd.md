```mermaid
game {
  int game_id PK "Primary Key"
  String external_id UK "Unique Key, non-null"
  String pool "Code pool, non-null"
  int length "Code length, non-null"
  Instant started "Date-time Started, non-null"
  boolean solved "Solved Flag, non-null"
  Instant last_played "Date-time of last guess"
  int exact_matches "Exact Matches in last guess"
  int near_matches "Near Matches in last guess"
}
```