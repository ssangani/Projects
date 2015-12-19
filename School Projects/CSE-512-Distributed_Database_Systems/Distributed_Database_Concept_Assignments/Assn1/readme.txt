Please get the attached 'Interface 02.py' script and the assignment question functions are defined in script as follows

3. Load_Ratings()
loadratings('ratings_table_name', '~\fakepath\file.dat', db_connection)
eg: loadratings('ratings', 'C:\\ratings.dat', con)

4. Range_Partition()
rangepartition('ratings_table_name', numberOfPartitions, db_connection)
eg: rangepartition('ratings', 5, con)

5. RoundRobin_Partition()
roundrobinpartition('ratings_table_name', numberOfPartitions, db_connection)
eg: roundrobinpartition('ratings', 5, con)

6. RoundRobin_Insert()
roundrobininsert('ratings_table_name', user_id, movie_id, rating, db_connection)
eg: roundrobininsert('ratings', 29, 11, 5, con)

7. Range_Insert()
rangeinsert('ratings_table_name', user_id, movie_id, rating, db_connection)
eg: rangeinsert('ratings', 29, 11, 5, con)

8. Delete Partitions()
delete_partitions('ratings_table_name', db_connection)
eg: delete_partitions('ratings', con)

Also following function will delete any table in given database
delete_table('ratings_table_name', db_connection)
eg: delete_table('ratings', con)

