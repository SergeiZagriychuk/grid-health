java -cp *;. org.openqa.grid.selenium.GridLauncher -role node -hub http://localhost:4444/grid/register -servlets com.qaprosoft.qa.StatusServlet -port 5555 -browser browserName=firefox,version=33,maxInstances=2,platform=WINDOWS -browser browserName=iexplore,version=9,maxInstances=1,platform=WINDOWS