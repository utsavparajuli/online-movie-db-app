# 2023-fall-cs122b-team-night
2023-fall-cs122b-team-night created by GitHub Classroom

### Demonstration video link - Project 3
 - https://youtu.be/knoGkAABcOw


### Member Contribution
- Daniel Bremner (Epsil-db): Task 4, Task 5
- Utsav Parajuli (utsavparajuli) : Task 1, Task 2, Task 3, Task 6

### Parsing optimizations
- Optimization 1:
  - Used batch size of 500 to batch together the queries to insert the new data parsed from the XML files into the database. Compared to the naive approach there was a time difference of over 10 minutes.
- Optimization 2:
  - Used hash maps for O(1) insert and search for the data read and stored from the parser. To check for inconsistencies this was a massive improvement in performance as we just had to check if a key exists in the map.
- Overall:
  - There was a time improvement of ~12 minutes compared to the naive approach. The overall run time of the parser is ~40 seconds now (locally).

### Parsing Inconsistency Ouput Statements
- Output stored in parser folder

### Prepared Statements
- Top20RatedServlet.java - line 57
- SubmitStarServlet.java - line 42
- SingleStarServlet.java - line 62
- SingleMovieServlet.java - line 68, 97, 114
- ShoppingCarServlet.java - line 68
- PaymentServlet.java - line 91
- MainPageServlet - line 57
- MovieListServlet - line 133, line 158, line 174
- LoginServlet.java - line 57
- EmployeeLoginServlet.java - 54
- ConfirmatoinServlet - line 55
- AddMovieServlet - line 33

### Extra Credit (hopefully linked by the time you see: Falbflix.wtf
<img width="1110" alt="image" src="https://github.com/uci-jherold2-fall23-cs122b/2023-fall-cs122b-team-night/assets/54547647/8b1e0e55-17fb-445d-94ab-2cfd72b9c2b9">
