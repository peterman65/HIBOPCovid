library(car) #Para el Recode
library(survival)
library(forecast)
library(cmprsk)
# Cargamos los datos en el formato para la herramienta online.
data_form=read.table(file=nombreFichero,sep=',',
                     colClasses=c("integer","numeric","numeric","character","character","character","character","character"))
names(data_form)=c("id","group_1","group_2","positive","adm_hos","adm_ICU","dis_ICU","dis_hos")


#Los warnings se almacenarán en esta variable
list_warnings=c()

#Grupos a factor
data_form[,2]=as.factor(data_form[,2])
data_form[,3]=as.factor(data_form[,3])

# Fechas en formato Date
for(i in 4:8){data_form[,i]=as.Date(data_form[,i],"%Y-%m-%d")}

#Quitamos de la base de datos a los pacientes que tienen NA en el id, alguna de las agrupaciones, o la fecha de positivo
index_na=which(is.na(data_form$id) | is.na(data_form$positive) )
if(length(index_na)>0){
  data_form=data_form[-index_na,]
  list_warnings=c(list_warnings,paste(length(index_na),'observations have been removed due to missing data in id or date of positive.'))
}

index_na=which(is.na(data_form$group_1) | is.na(data_form$group_2) )
if(length(index_na)>0){
  data_form=data_form[-index_na,]
  #Avisamos del número de observaciones retiradas por NA
  list_warnings=c(list_warnings,paste(length(index_na),'observations have been removed due to a missing value in a grouping variable.'))
}

numClasesG2= length(unique(data_form[,3]))

cat(paste("Number of clases in G2: ", numClasesG2,"\n", sep= " "))

#print(summary(data_form))
#cat(paste("Número de reg??stros válidos: ", dim(data_form)[1],"\n", sep= " "))
cat(paste("Number of valid registers: ", dim(data_form)[1],"\n", sep= " "))
