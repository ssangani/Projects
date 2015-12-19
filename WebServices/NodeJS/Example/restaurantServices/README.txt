Installation
1. This project assumes that you have MySQL and NodeJS installed on your machine
   You can download NodeJS for your system from: https://nodejs.org/en/download/
   Also developer version of MySQL can be found at: http://dev.mysql.com/downloads/
   Both sites are complete with installation instructions for each tool
   Please follow the instructions and have system ready for the application deployment
2. Once system is ready, extract the 'restaurantServices.zip' to a directory of your choosing which we will refer to as 'BASEDIR'
3. In '~/BASEDIR/restaurantServices/env.js' modify the values for database connection settings as per your local settings
3. Open command prompt and go to '~/BASEDIR/restaurantServices' and run command 'node setup.js'
   This will set up database schema required and inject some initial data
   If you prefer to do it manually, running scripts 'deploy.sql' and after that 'initData.sql' from MySQL achieves same result
4. Now that your setup is completed, run command 'node service.js' and it will start a local web server on your machine
5. Please refer to API documentation found at '~/BASEDIR/restaurantServices/DOCS.txt' for reference on how to use the system 
 