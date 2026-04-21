# Variables
$glassfishBin   = "C:\glassfish8\glassfish\bin"   # adjust to your GlassFish bin folder
$appName        = "jobfinder"
$warFile        = "target\jobfinder.war"
$adminUser      = "admin"
$adminPassword  = "Newmark@8"
$pwdFile        = "$glassfishBin\gfpass.txt"

# Create/overwrite password file
"AS_ADMIN_PASSWORD=$adminPassword" | Out-File -FilePath $pwdFile -Encoding ASCII


Write-Host "Undeploying $appName..."
asadmin.bat --user $adminUser --passwordfile $pwdFile undeploy $appName

Write-Host "Building project with Maven..."
Set-Location "C:\Users\aubre\IdeaProjects\Jakarta-Projects\Job-Finder"
mvn.cmd clean package

Write-Host "Deploying $warFile..."
Set-Location $glassfishBin
& .\asadmin.bat --user $adminUser --passwordfile $pwdFile deploy "C:\Users\aubre\IdeaProjects\Jakarta-Projects\Job-Finder\$warFile"

Set-Location "C:\Users\aubre\IdeaProjects\Jakarta-Projects\Job-Finder"
Write-Host "Deployment complete!"