## Añado cosas mías para cargar datos
#rm(list=ls())
#assign("last.warning", NULL, envir = baseenv())
#setwd('/home/miguel/Dropbox/valida_covid_simulador')
#library(car) #Para el Recode
#library(survival)
#library(forecast)
#library(cmprsk)


##################
##### INPUTS #####
##################

#Establecer fechas de estimación
#t_I='2020-07-01'
#t_F='2021-01-07'

# Crear un nuevo escenario para los nuevos positivos a partir del día de fecha_inicio_periodo_positivos anterior
#automatic_scenario=T
#Ejemplo para custom_scenario rep(500,7)
#custom_scenario=F
# custom_scenario=seq(100,1400,length.out=14)
#frequency_series=7
#threshold_proportion_groups_custom_scenario=30

#Predict initial date
#t_0='2021-01-08'
#Predict final date
#t_1='2021-01-21'


# ¿Simular?
#simular=T

####################################
## Data Processing
####################################

#Pasamos a formato fecha los tiempos dados como input.
t_I=as.Date(t_I)
t_F=as.Date(t_F)
t_0=as.Date(t_0)
t_1=as.Date(t_1)

#Si hay un custom scenario redefine t_1
if(!isFALSE(custom_scenario)){
  t_1=t_0+length(custom_scenario)-1
}

# Cargamos los datos en el formato para la herramienta online.
#data_form=read.table(file='datos_formateados.csv',sep=',',
#                     colClasses=c("integer","numeric","numeric","character","character","character","character","character"))
#names(data_form)=c("id","group_1","group_2","positive","adm_hos","adm_ICU","dis_ICU","dis_hos")

#Los warnings se almacenarán en esta variable
#list_warnings=c()

#Grupos a factor
#data_form[,2]=as.factor(data_form[,2])
#data_form[,3]=as.factor(data_form[,3])

# Fechas en formato Date
#for(i in 4:8){data_form[,i]=as.Date(data_form[,i],"%Y-%m-%d")}

#Quitamos de la base de datos a los pacientes que tienen NA en el id, alguna de las agrupaciones, o la fecha de positivo
#index_na=which(is.na(data_form$id) | is.na(data_form$positive) )
#if(length(index_na)>0){
#  data_form=data_form[-index_na,]
#  list_warnings=c(list_warnings,paste(length(index_na),'observations have been removed due to missing data in id or date of positive.'))
#}

#index_na=which(is.na(data_form$group_1) | is.na(data_form$group_2) )
#if(length(index_na)>0){
#  data_form=data_form[-index_na,]
#  #Avisamos del número de observaciones retiradas por NA
#  list_warnings=c(list_warnings,paste(length(index_na),'observations have been removed due to a missing value in a grouping variable.'))
#}


#Testeamos coherencia de los datos
index_1=which(data_form$positive>data_form$adm_hos)
index_2=which(data_form$adm_hos>data_form$dis_hos)
index_4=which(data_form$adm_ICU>data_form$dis_ICU)
index_5=which(data_form$dis_ICU>data_form$dis_hos)
index_6=which(is.na(data_form$adm_ICU) & !is.na(data_form$dis_ICU))
index_7=which(is.na(data_form$adm_hos) & !is.na(data_form$dis_hos))
index_8=which(is.na(data_form$adm_hos) & !is.na(data_form$adm_ICU))

incoherent_indexes=unique(c(index_1,index_2,index_4,
                         index_5,index_6,index_7,index_8))

# Si hay observaciones incoherentes las quitamos 
if(length(incoherent_indexes)>0){
  data_form=data_form[-incoherent_indexes,]
  list_warnings=c(list_warnings,paste(length(incoherent_indexes),'observations have been removed due to incoherences in some dates.'))
}

#Si t_0 es mayor que el máximo de las fechas introducidas dar un warnings
if(t_0>max(data_form$positive)+1){
  list_warnings=c(list_warnings,'Warning: t_0 is posterior to the last positive date by more than a day.')
}

#Error, no complete trajectories to estimate every parameter of the model
if(sum(!is.na(data_form$dis_hos) & is.na(data_form$dis_ICU))==0 & sum(!is.na(data_form$dis_hos) & !is.na(data_form$dis_ICU))==0){
  list_warnings=c(list_warnings,'Error: No patients fulfilled complete trajectories through ICU and hospital, so no estimation can be done.')
}

### Cogemos los datos que han dado positivo en el período de estimaciones
data=data_form[which(data_form$positive>=t_I & data_form$positive<=t_F),]

if(min(table(data$group_1))==0 | min(table(data$group_2))==0){
  list_warnings=c(list_warnings,'Warning: no patients in some of the predefined groups between t_I and t_F.')
  }

data$days_before_hos=as.numeric(data$adm_hos-data$positive)
data$days_hos=as.numeric(data$dis_hos-data$adm_hos)
data$days_before_ICU=as.numeric(data$adm_ICU-data$adm_hos)
data$days_ICU=as.numeric(data$dis_ICU-data$adm_ICU)
data$days_post_ICU=as.numeric(data$dis_hos-data$dis_ICU)

# Construimos una variable indicatriz de la hospitalización y otra para la UCI
data <- within(data, {hospital <- Recode(is.na(adm_hos), 'TRUE = "no"; FALSE = "yes";',as.factor=TRUE)})
# levels(data$hospital)=c("no","yes")

data <- within(data, {ICU <- Recode(is.na(adm_ICU), 'TRUE = "no"; FALSE = "yes";', as.factor=TRUE)})
# levels(data$ICU)=c("no","yes")

##################
## ESTIMACIONES ##
##################

##Vamos a hacer las estimaciones para las dos agrupaciones
for(index_for_list in 1:2){
  # index_for_list=1
  group_variable=c('group_1','group_2')[index_for_list]

####
## Primer tiempo
####

# Vector of event ocurrence, 1 for admission in hospital prior to t_0, 0 otherwise
index_hos_yes=(data$adm_hos<t_0)
event=rep(0,dim(data)[1])
event[index_hos_yes]=1

#Vector for days_until event or until t_0 if the patient has not been admitted to hospital
days_until_event=rep(NA,length(event))
days_until_event[event==0]=t_0-data$positive[event==0]-1
days_until_event[event==1]=data$adm_hos[event==1]-data$positive[event==1]


#Cure vector. We do not know exactly which patients cannot experience the outcome eventually since we lose the follow-up
cured=rep(0,dim(data)[1])

data.toCM= as.data.frame(cbind(days_until_event, event,cured))

#Function to fit non-parametric cured-models with cure partially known 
S_NPCPK_to_Survival = function (data=data) {
  
  N = nrow(data)
  
  #Order data
  data.ot <- data[order(data[, 1]), ]
  t = data.ot[,1]
  d = data.ot[,2]
  nu = data.ot[,3]
  cum.nu = cumsum(nu)
  
  S = rep(1, N)
  
  #Estimator of survival function
  for (i in 2:N) {
    if(d[i]==0) {S[i] = S[i-1]}
    if(d[i]==1) {S[i]  = S[i-1] * (1 - 1/(N - i + 1 + cum.nu[i-1]))}}
  
  #Probability of experimenting the event
  p  =  1- min(S)
  
  #Survival of the time until event
  survivalF=(S-(1-p))/p
  times=t
  
  #Extract indexes of the first appeareances of the integers times to build te survival function table
  index_for_S=sapply(unique(times), function(x) min(which(times==x)))
  result=cbind(times[index_for_S],survivalF[index_for_S])

  #Discard the out of support points in the survival function
  index_end_support=which(result[,2]==0)
  if(length(index_end_support)==0){
    index_end_support=which(result[,1]==max(t[which(d==1)]))
  } else{
    index_end_support=min(index_end_support)-1
    }
  result=result[1:index_end_support,]
  
  #To transform P(T>=t) to P(T>t)
  result[,2]=c(result[2:length(result[,2]),2],0)
  
  # #To delete the no changing probability points (in order to interpolate later)
  rr=result[,2]
  indexes_bool=c(F,rr[-1]==rr[-length(rr)])
  result=result[which(indexes_bool==F),]
  
  return(list(S, p, t,result))
}


#Fit the CM for every group
probabilities_hos_by_group=c()
times_cure_model_1_list=list()
for(i in 1:length(levels(data[[group_variable]]))){
  if(sum(data.toCM[which(data[[group_variable]]==i),]$event)==0){
    
    list_warnings=c(list_warnings,paste('No observations in group',i,'when fitting the cure model for ',group_variable,'. All groups have been used for the fit.'))
    
    resCM=S_NPCPK_to_Survival(data.toCM)
    
  } else{

    resCM=S_NPCPK_to_Survival(data.toCM[which(data[[group_variable]]==i),])
  }
  
  #Store probability of cure for the group
  probabilities_hos_by_group=c(probabilities_hos_by_group,resCM[[2]])
  #Store Time distribution until event
  times_cure_model_1_list[[i]]=resCM[[4]]
}



####
## Segundo tiempo
####
## (De hospital a casa o a UCI)

# Identificamos los índices de la observaciones para crear el código de los eventos para el competing risks.
ind_hos_before_t_0=(data$hospital=='yes' & data$adm_hos<t_0)
data_hos=data[which(ind_hos_before_t_0),]


index_ICU_yes=(data_hos$ICU=='yes' & data_hos$adm_ICU<t_0)
index_hos_dis=(data_hos$ICU=='no' & data_hos$dis_hos<t_0)

# Creamos el vector de eventos para las observaciones
event=rep(0,dim(data_hos)[1])
event[which(index_ICU_yes)]=1
event[which(index_hos_dis)]=2

#Creamos un vector con los días hasta el evento, en función del mismo se calculará de manera diferente.
days_until_event=rep(-1,length(event))
days_until_event[which(event==0)]=t_0-data_hos$adm_hos[which(event==0)]-1
days_until_event[which(event==1)]=data_hos$adm_ICU[which(event==1)]-data_hos$adm_hos[which(event==1)]
days_until_event[which(event==2)]=data_hos$dis_hos[which(event==2)]-data_hos$adm_hos[which(event==2)]


#Vamos a hacer un pequeño data frame con lo que necesitamos para los competing risks
aux=cbind(event,data_hos[[group_variable]],days_until_event)
aux=data.frame(aux)
colnames(aux)=c("event","group","days_until_event")


## Ajustamos los riesgos competitivos a cada grupo
times_comp_risk_2_list=list()
times_comp_risk_2_list_adm_ICU=list()
times_comp_risk_2_list_dis_hos=list()


for(i in 1:length(levels(data[[group_variable]]))){
  if(length(which(aux[which(aux$group==i),]$event==1))==0 | length(which(aux[which(aux$group==i),]$event==2))==0){
    
    list_warnings=c(list_warnings,paste('No observations in group',i,'when estimating the competing risks estimation for ',group_variable,'. All groups have been used for the fit.'))
    
    resCumInc <- cuminc(ftime = aux$days_until_event,  # failure time variable
                        fstatus = as.factor(aux$event),  # variable with distinct codes for different causes of failure
                        rho     = 0, # Power of the weight function used in the tests.
                        cencode = 0 # value of fstatus variable which indicates the failure time is censored.
    )
    
  } else{
    resCumInc <- cuminc(ftime = aux$days_until_event[which(aux$group==i)],  # failure time variable
                        fstatus = as.factor(aux$event)[which(aux$group==i)],  # variable with distinct codes for different causes of failure
                        rho     = 0, # Power of the weight function used in the tests.
                        cencode = 0 # value of fstatus variable which indicates the failure time is censored.
    )
  }
  
  times_comp_risk_2_list[[i]]=timepoints(resCumInc, times = 0:max(aux$days_until_event))$est
  times_comp_risk_2_list_adm_ICU[[i]]=timepoints(resCumInc, times = 0:max(aux$days_until_event))$est[1,]
  times_comp_risk_2_list_dis_hos[[i]]=timepoints(resCumInc, times = 0:max(aux$days_until_event))$est[2,]
}

#Función que dado un objeto de tipo est resultado de ajustar un modelos de competing risk devuelve la suma del máximo de las probabilidades acumuladas en los dos eventos
normalize=function(list_competing){
  d=sum(apply(list_competing,1, function(x) max(x,na.rm=T)))
  return(d)
}

#Calculamos en cada grupo cual es  la constante normalizadora de la probabilidad sobre el máximo de las estimaciones mediante competing risk
constant_to_normalize_probability_by_group=unlist(lapply(times_comp_risk_2_list,normalize))

#Esto son las probabilidades de ir a UCI, tomamos el máximo normalizando por la constante de cada grupo
probabilities_ICU_by_group=unlist(lapply(times_comp_risk_2_list_adm_ICU, function(x) max(x,na.rm=T)))/constant_to_normalize_probability_by_group


####
## Tercer tiempo
####

## Tiempo para los que están en la UCI
data_ICU=data[which(data$adm_ICU<t_0),]

#Generamos el tiempo observado hasta evento o censura
time_surv=rep(NA,dim(data_ICU)[1])
index_dis_ICU=which(data_ICU$dis_ICU<t_0)

time_surv[index_dis_ICU]=data_ICU$dis_ICU[index_dis_ICU]-data_ICU$adm_ICU[index_dis_ICU]
if(length(index_dis_ICU)>0){
  time_surv[-index_dis_ICU]=t_0-data_ICU$adm_ICU[-index_dis_ICU]-1
} else{ 
  list_warnings=c(list_warnings("No patients discharged from ICU before t_0"))
}



#También el vector de eventos
event_surv=rep('no',length(time_surv))
event_surv[index_dis_ICU]='yes'

#Y los distintos grupos
group_surv=data_ICU[[group_variable]]


#Creamos una lista con el objeto (tabla) de supervivencia para cada grupo
final_object=list()
for(i in 1:length(levels(group_surv))){
  if(length(which(event_surv[which(group_surv==i)]=='yes'))==0){
    list_warnings=c(list_warnings,paste('No observations in group',i,'when estimating ICU times for',group_variable,'. All groups have been used for the fit.'))
    fit1=Surv(time_surv,
              as.factor(event_surv)=='yes')
  } else{
  fit1=Surv(time_surv[group_surv==levels(group_surv)[i]],
            as.factor(event_surv[group_surv==levels(group_surv)[i]])=='yes')}
  fit2=survfit(fit1~1)
  result=cbind(summary(fit2)$time,summary(fit2)$surv)
  colnames(result)=c('time','survival')
  final_object[[i]]=result
}
ICU_times=final_object


####
## Cuarto tiempo
####

## Tiempo post UCI
data_post_ICU=data[which(data$dis_ICU<t_0),]

#Generamos el tiempo observado hasta evento o censura
time_surv=rep(NA,dim(data_post_ICU)[1])
index_dis_hos=which(data_post_ICU$dis_hos<t_0)

time_surv[index_dis_hos]=data_post_ICU$dis_hos[index_dis_hos]-data_post_ICU$dis_ICU[index_dis_hos]

if(length(index_dis_hos)>0){
  time_surv[-index_dis_hos]=t_0-data_post_ICU$dis_ICU[-index_dis_hos]-1
} else{ 
  list_warnings=c(list_warnings("No patients discharged from hospital after ICU before t_0"))
}


#También el vector de eventos
event_surv=rep('no',length(time_surv))
event_surv[index_dis_hos]='yes'

#Y los distintos grupos
group_surv=data_post_ICU[[group_variable]]

#Creamos una lista con el objeto (tabla) de supervivencia para cada grupo
final_object=list()
for(i in 1:length(levels(group_surv))){
  #Si no hay datos en el grupo hacemos las estimaciones con todos.
  if(length(which(event_surv[which(group_surv==i)]=='yes'))==0){
    list_warnings=c(list_warnings,paste('No observations in group',i,'when estimating post ICU times for',group_variable,'. All groups have been used for the fit.'))
    fit1=Surv(time_surv,
              as.factor(event_surv)=='yes')
  } else{
    fit1=Surv(time_surv[group_surv==levels(group_surv)[i]],
              as.factor(event_surv[group_surv==levels(group_surv)[i]])=='yes')}
  
  
  fit2=survfit(fit1~1)
  result=cbind(summary(fit2)$time,summary(fit2)$surv)
  colnames(result)=c('time','survival')
  final_object[[i]]=result
}
post_ICU_times=final_object


##########
## Tan solo hemos de transformar las tablas de las funciones de supervivencia para que estén en el formato del programa de Javier
##########

# Si los primeros tiempos no se pueden estimar, se pone 1
# Se interpola linealmente
# Después del tiempo máximo observado se pone 0 
# Revisar si esta sigue siendo la política actual:
#(esto luego se compensa en el simulador haciendo que los pacientes largos pasen a tener un determinado cuantil del tiempo para no darlo de alta inmediatamente)

# Función que dada una tabla correspondiente a lo que hemos guardado en cada uno de los objetos dentro de las listas anteriores (F de supervivencia)
# Devuelve la misma en un formato estándarizado para el simulador e interpolando la distribución

interpolate_table=function(tabla,len_max=1000){
  len=len_max
  #Tabla es la tabla de la estimación KM
  dummy=tabla
  # Añadimos por debajo en la tabla el último tiempo y valor que consideramos
  dummy=rbind(dummy,c(len,0))
  
  #Si el tiempo mínimo de la función estimada por KM no es cero, añadimos los tiempos anteriores y 1s por encima
  if(min(dummy[,1])!=0){
    tmin=min(dummy[,1])
    for(i in (tmin-1):0){
      dummy=rbind(c(i,1),dummy)
    }
  }
  #Interpolamos lineal en ese tramo
  interpolation=approx(dummy,xout=0:(len-1))
  
  #Cogemos el máximo de la tabla original, para identificar el punto máximo del soporte de la f de KM
  index_zero=max(tabla[,1])
  
  #Ponemos un cero al valor posterior de ese índice y 0 a los siguientes (Escrito en dos pasos porque antes se ponía -1)
  interpolation$y[index_zero+1]=0
  interpolation$y[(index_zero+2):len_max]=-1
  
  return(interpolation$y)
}


## Las tablas a devolver

#Función que dado un vector, devuelve otro vector con solo los puntos en los que cambia 
equivalent_vector=function(vect){
  indexes_bool=c(F,vect[-1]==vect[-length(vect)])
  return(vect[which(indexes_bool==F)])
}

#Función que dado un vector de las filas del resultado de los competing lo interpola y lo pone en el formato deseado

interpolate_competing=function(vector_form_competing,len_max=1000){
  result_p=vector_form_competing
  result_p=1-result_p/max(result_p,na.rm=T)
  result_p=equivalent_vector(result_p) #Para aplicar la función del interpolador lineal
  t_p=cbind(as.numeric(names(result_p)),as.vector(result_p))
  return(interpolate_table(t_p,len_max))
}


#Para el tiempo hasta ir a la UCI
a=NULL
n_rows_file=max(unlist(lapply(times_comp_risk_2_list_adm_ICU,length)))+1
for(index_vector in 1:length(times_comp_risk_2_list_adm_ICU)){
  a=cbind(a,interpolate_competing(times_comp_risk_2_list_adm_ICU[[index_vector]],n_rows_file))
}
b=format(a, scientific = FALSE)
name_file=paste('dias_hospital_antes_uci_',index_for_list,'.csv',sep='')
write.table(b,file=paste(carpetaResultados,name_file,sep=""),row.names = F, col.names=F,sep=',',quote = FALSE)


#Para el tiempo hasta ir al alta desde el primer hospital
a=NULL
n_rows_file=max(unlist(lapply(times_comp_risk_2_list_dis_hos,length)))+1
for(index_vector in 1:length(times_comp_risk_2_list_dis_hos)){
  a=cbind(a,interpolate_competing(times_comp_risk_2_list_dis_hos[[index_vector]],n_rows_file))
}
b=format(a, scientific = FALSE)
name_file=paste('dias_hospital_sin_uci_',index_for_list,'.csv',sep='')
write.table(b,file=paste(carpetaResultados,name_file,sep=""),row.names = F, col.names=F,sep=',',quote = FALSE)


#Las de Kaplan-Meier

#Para el tiempo hasta ir al hospital
n_rows_file=max(unlist(lapply(times_cure_model_1_list,max)))+2

a=sapply(times_cure_model_1_list, function(x) interpolate_table(x,n_rows_file))

a=matrix(unlist(a),ncol=length(times_cure_model_1_list))
b=format(a, scientific = FALSE)
name_file=paste('dias_antes_hospital_',index_for_list,'.csv',sep='')
write.table(b,file=paste(carpetaResultados,name_file,sep=""),row.names = F, col.names=F,sep=',',quote = FALSE)


n_rows_file=max(unlist(lapply(ICU_times,max)))+2
a=sapply(ICU_times, function(x) interpolate_table(x,n_rows_file))
a=matrix(unlist(a),ncol=length(ICU_times))
b=format(a, scientific = FALSE)
name_file=paste('dias_uci_',index_for_list,'.csv',sep='')
write.table(b,file=paste(carpetaResultados,name_file,sep=""),row.names = F, col.names=F,sep=',',quote = FALSE)

n_rows_file=max(unlist(lapply(post_ICU_times,max)))+2
a=sapply(post_ICU_times,function(x) interpolate_table(x,n_rows_file))
a=matrix(unlist(a),ncol=length(post_ICU_times))
b=format(a, scientific = FALSE)
name_file=paste('dias_hospital_tras_uci_',index_for_list,'.csv',sep='')
write.table(b,file=paste(carpetaResultados,name_file,sep=""),row.names = F, col.names=F,sep=',',quote = FALSE)

#Tenemos que generar también las tablas de probabilidades
tabla_prob=rbind(probabilities_hos_by_group,probabilities_ICU_by_group)
name_file=paste('probabilidades_hospital_uci_',index_for_list,'.csv',sep='')
tabla_prob=format(tabla_prob, scientific = FALSE)
write.table(tabla_prob,file=paste(carpetaResultados,name_file,sep=""),row.names = F, col.names=F,sep=',',quote = FALSE)

}


##############
# Generación de los nuevos positivos por grupo_2
##############

#Para las dos siguientes secciones volvemos a trabajar con la base de datos total, no solo entre t_I y t_F
data=data_form

# Función que extrae los positivos por grupo para una fecha y el número total
# Notar que el número total no tiene porque coincidir con la suma por grupos de edad debido a NA's (Ahora sí porque hemos quitado los NA's)
daily_positive_by_group=function(date_input){
  index=which(data$positive==date_input)
  aux=table(data[index,]$group_2)
  return(c(aux,length(index)))
}

#Si hemos puesto un escenario personalizado
if(!isFALSE(automatic_scenario)) {
  
  
  ### Esta es la función de AC para predecir
  PredCont<-function(Cont,  conflevel=0.95, hpred=14, freq=7)
  {
    if (is.ts(Cont)==F) Cont<-ts(Cont, frequency=freq)
    if (frequency(Cont)==1)  gamma<-FALSE else gamma<-NULL
    aux<-newHoltWinters(Cont, gamma = gamma,  seasonal = c("additive"))
    paux<-predict(aux, n.ahead=hpred, prediction.interval=TRUE, level=conflevel)
    pauxMean<-apply(cbind(0,paux[,1]), MARGIN=1,  max)
    pauxUpper<-apply(cbind(0,paux[,2]), MARGIN=1,  max)
    pauxLower<-apply(cbind(0,paux[,3]), MARGIN=1,  max)
    return(list(pred=cbind(pauxMean, pauxLower, pauxUpper), message=aux$message))
  }
  
  newHoltWinters<-function (x, alpha = NULL, beta = NULL, gamma = NULL, seasonal = c("additive"), 
                            start.periods = 2, l.start = NULL, b.start = NULL, s.start = NULL, 
                            optim.start = c(alpha = 0.3, beta = 0.1, gamma = 0.1), optim.control = list()) 
  {
    x <- as.ts(x)
    seasonal <- match.arg(seasonal)
    f <- frequency(x)
    if (is.null(gamma) || gamma > 0) {
      if (start.periods < 2) 
        stop("need at least 2 periods to compute seasonal start values")
    }
    if (!is.null(gamma) && is.logical(gamma) && !gamma) {
      expsmooth <- !is.null(beta) && is.logical(beta) && !beta
      if (is.null(l.start)) 
        l.start <- if (expsmooth) 
          x[1L]
      else x[2L]
      if (is.null(b.start)) 
        if (is.null(beta) || !is.logical(beta) || beta) 
          b.start <- x[2L] - x[1L]
        start.time <- 3 - expsmooth
        s.start <- 0
    }
    else {
      start.time <- f + 1
      wind <- start.periods * f
      st <- decompose(ts(x[1L:wind], start = start(x), frequency = f), 
                      seasonal)
      if (is.null(l.start) || is.null(b.start)) {
        dat <- na.omit(st$trend)
        cf <- coef(.lm.fit(x = cbind(1, seq_along(dat)), 
                           y = dat))
        if (is.null(l.start)) 
          l.start <- cf[1L]
        if (is.null(b.start)) 
          b.start <- cf[2L]
      }
      if (is.null(s.start)) 
        s.start <- st$figure
    }
    lenx <- as.integer(length(x))
    if (is.na(lenx)) 
      stop("invalid length(x)")
    len <- lenx - start.time + 1
    hw <- function(alpha, beta, gamma) .C(stats:::C_HoltWinters, as.double(x), 
                                          lenx, as.double(max(min(alpha, 1), 0)), as.double(max(min(beta, 
                                                                                                    1), 0)), as.double(max(min(gamma, 1), 0)), as.integer(start.time), 
                                          as.integer(!+(seasonal == "multiplicative")), as.integer(f), 
                                          as.integer(!is.logical(beta) || beta), as.integer(!is.logical(gamma) || 
                                                                                              gamma), a = as.double(l.start), b = as.double(b.start), 
                                          s = as.double(s.start), SSE = as.double(0), level = double(len + 
                                                                                                       1L), trend = double(len + 1L), seasonal = double(len + 
                                                                                                                                                          f))
    #    ind<-seq(0.1, 0.9, by=0.1)
    ind<-seq(0.01, 0.99, by=0.01)
    message<-NULL
    if (is.null(gamma)) 
    {
      error <- function(p) hw(p[1L], p[2L], p[3L])$SSE
      #               sol <- optim(optim.start, error, method = "L-BFGS-B", 
      #                 lower = c(0, 0, 0), upper = c(1, 1, 1), control = optim.control)
      sol <- nlminb(optim.start, objective=error, lower = c(0, 0, 0), upper = c(1, 1, 1), control = optim.control)
      alpha <- sol$par[1L]
      beta <- sol$par[2L]
      gamma <- sol$par[3L]
      if ((sol$convergence >0)|| any(sol$par < 0 | sol$par > 1))
      {
        cat("Convergence  is not achieved. A grid search is being carried out. It may take a few seconds.", fill=T)
        message<- "Convergence  is not achieved. A grid search is being carried out. It may take a few seconds."  			
        aux<-gridSearch(fun=error, levels=list(ind,ind,ind))$minlevels
        alpha <- aux[1L]
        beta <- aux[2L]
        gamma <- aux[3L]
      }
    }
    else 
    {
      error <- function(p) hw(p[1L], p[2L], gamma)$SSE
      sol <- nlminb(c(optim.start["alpha"], optim.start["beta"]), objective=error, 
                    lower = c(0,  0), upper = c(1, 1), control = optim.control)
      alpha <- sol$par[1L]
      beta <- sol$par[2L]
      if ((sol$convergence >0)|| any(sol$par < 0 | sol$par > 1))
      {
        cat("Convergence  is not achieved. A grid search is being carried out. It may take a few seconds.", fill=T)
        message<- "Convergence  is not achieved. A grid search is being carried out. It may take a few seconds."  			
        aux<-gridSearch(fun=error, levels=list(ind,ind))$minlevels
        alpha <- aux[1L]
        beta <- aux[2L]
      }
      
    }
    final.fit <- hw(alpha, beta, gamma)
    fitted <- ts(cbind(xhat = final.fit$level[-len - 1], level = final.fit$level[-len - 
                                                                                   1], trend = if (!is.logical(beta) || beta) 
                                                                                     final.fit$trend[-len - 1], season = if (!is.logical(gamma) || 
                                                                                                                             gamma) 
                                                                                       final.fit$seasonal[1L:len]), start = start(lag(x, k = 1 - 
                                                                                                                                        start.time)), frequency = frequency(x))
    if (!is.logical(beta) || beta) 
      fitted[, 1] <- fitted[, 1] + fitted[, "trend"]
    if (!is.logical(gamma) || gamma) 
      fitted[, 1] <- fitted[, 1] + fitted[, "season"]
    structure(list(fitted = fitted, x = x, alpha = alpha, beta = beta, 
                   gamma = gamma, coefficients = c(a = final.fit$level[len + 
                                                                         1], b = if (!is.logical(beta) || beta) final.fit$trend[len + 
                                                                                                                                  1], s = if (!is.logical(gamma) || gamma) final.fit$seasonal[len + 
                                                                                                                                                                                                1L:f]), seasonal = seasonal, SSE = final.fit$SSE, 
                   call = match.call(),message=message), class = "HoltWinters")
  }
  
  
  
  
  
  gridSearch<-function (fun, levels, ..., lower, upper, npar = 1L, n = 5L, 
                        printDetail = TRUE, method = NULL, mc.control = list(), cl = NULL, 
                        keepNames = FALSE, asList = FALSE) 
  {
    if (keepNames) 
      lNames <- names(levels)
    if (!is.null(method)) {
      method <- tolower(method[1L])
    }
    else if (!is.null(cl)) {
      method <- "snow"
    }
    else method <- "loop"
    if (method == "snow" && is.null(cl)) {
      method <- "loop"
      warning("no cluster ", sQuote("cl"), " passed for method ", 
              sQuote("snow"), ": will use method ", 
              sQuote("loop"))
    }
    n <- makeInteger(n, "n", 2L)
    if (missing(levels) && !missing(lower) && !missing(upper)) {
      lower <- as.numeric(lower)
      upper <- as.numeric(upper)
      if (length(lower) > 1L && length(upper) == 1L) {
        upper <- rep(upper, length(lower))
      }
      else if (length(lower) == 1L && length(upper) > 1L) {
        lower <- rep(lower, length(upper))
      }
      else if (length(lower) == 1L && length(upper) == 1L) {
        lower <- rep(lower, npar)
        upper <- rep(upper, npar)
      }
      else if (length(lower) < 1L || length(upper) < 1L) {
        stop("'lower' and 'upper' must have non-zero length")
      }
      if (length(lower) != length(upper)) 
        stop("'lower' and 'upper' must be of same length")
      if (any(lower > upper)) 
        stop("'lower' must not be greater than 'upper'")
      npar <- length(lower)
      levels <- vector("list", length = length(lower))
      for (i in seq_len(npar)) levels[[i]] <- seq(lower[[i]], 
                                                  upper[[i]], length.out = max(n, 2L))
    }
    np <- length(levels)
    res <- vector("list", np)
    rep.fac <- 1L
    nl <- sapply(levels, length)
    nlp <- prod(nl)
    if (printDetail) {
      if (np < 5L) 
        msg <- paste(nl, collapse = ", ")
      else {
        msg <- paste(c(nl[seq_len(4L)], "..."), collapse = ", ")
      }
      msg <- paste(np, " variables with ", msg, " levels: ", 
                   nlp, " function evaluations required.", sep = "")
      message(msg)
    }
    for (i in seq_len(np)) {
      x <- levels[[i]]
      nx <- length(x)
      nlp <- nlp/nx
      res[[i]] <- x[rep.int(rep.int(seq_len(nx), rep.int(rep.fac, 
                                                         nx)), nlp)]
      rep.fac <- rep.fac * nx
    }
    if (keepNames) 
      names(res) <- lNames
    nlp <- prod(nl)
    lstLevels <- vector("list", length = nlp)
    for (r in seq_len(nlp)) {
      lstLevels[[r]] <- if (asList) 
        as.list(sapply(res, `[[`, r))
      else sapply(res, `[[`, r)
    }
    if (method == "multicore") {
      mc.settings <- mcList(mc.control)
      results <- mclapply(lstLevels, fun, ..., mc.preschedule = mc.settings$mc.preschedule, 
                          mc.set.seed = mc.settings$mc.set.seed, mc.silent = mc.settings$mc.silent, 
                          mc.cores = mc.settings$mc.cores, mc.cleanup = mc.settings$mc.cleanup)
    }
    else if (method == "snow") {
      if (is.numeric(cl)) {
        cl <- makeCluster(c(rep("localhost", cl)), 
                          type = "SOCK")
        on.exit(stopCluster(cl))
      }
      results <- clusterApply(cl, lstLevels, fun, ...)
    }
    else {
      results <- lapply(lstLevels, fun, ...)
    }
    results <- unlist(results)
    i <- try(which.min(results))
    if (inherits(i, "try-error") || any(is.na(i)) || length(i) == 
        0L) {
      warning("cannot compute minimum (NA values in results, ...)")
      list(minfun = NA, minlevels = NA, values = results, levels = lstLevels)
    }
    else {
      list(minfun = results[i], minlevels = lstLevels[[i]], 
           values = results, levels = lstLevels)
    }
  }
  
  makeInteger <- function(x, label, min = 1L) {
    x <- suppressWarnings(as.integer(x))
    if (is.na(x) || x < min)
      stop(sQuote(label), " must be an integer not smaller than ", min)
    x
  }
  
  #Sacamos todos los nuevos positivos diarios del período de estimaciones.
  positives_by_group_last_days=t(sapply(seq(t_I,t_0-1,by='days'),daily_positive_by_group))
  
  #Predecimos para cada grupo por separado y almacenamos en una tabla de contingencia, filas dias, columnas grupos
  predictions_by_group=matrix(NA,ncol=length(levels(data$group_2)),nrow=as.numeric(t_1-t_0+1))
  

  # Seleccionamos el número de días para hacer HW con ese número de días previo.
  #Si es mayor que 28 y 2*periodo, nos quedamos con el máximo. Si esta entre medias nos quedamos con lo que haya.
  # Si es menos que el mínimos mostramos error y no calculamos.
  message_error=F
  if(dim(positives_by_group_last_days)[1]>=max(28,2*frequency_series)){
    threshold_days=max(28,2*frequency_series)
  } else if(dim(positives_by_group_last_days)[1]>=min(28,2*frequency_series)){
    threshold_days=dim(positives_by_group_last_days)[1]
  } else {
    message_error='Error, less than two periods of time for estimation of the new positives.'
    print(message_error)
    list_warnings=c(list_warnings,message_error)
    }

  if(isFALSE(message_error)){
  indexes_to_predict=(dim(positives_by_group_last_days)[1]-threshold_days+1):dim(positives_by_group_last_days)[1]

  for(column in 1:length(levels(data$group_2))){
    
    prediction_HW=PredCont(positives_by_group_last_days[indexes_to_predict,column],hpred=as.numeric(t_1-t_0+1),freq=frequency_series)
    predictions_by_group[,column]=round(prediction_HW[[1]][,1])
    
    if(!is.null(prediction_HW[[2]])){
      list_warnings=c(list_warnings,prediction_HW[[2]])
      }
    
    
  }
  #Exportación de los nuevos positivos
  write.table(predictions_by_group,file=paste(carpetaResultados,'nuevos_positivos_escenario.csv',sep=""),row.names = F, col.names=F,sep=',')
  }
}


if(isFALSE(automatic_scenario) & !isFALSE(custom_scenario)) {
   
  #Cogemos un número de días anterior no muy lejano para estimar las proporciones de los grupos
  t_init_prop_groups=t_0-threshold_proportion_groups_custom_scenario
  if(t_0-1-t_I<threshold_proportion_groups_custom_scenario){
    #Si el período de estimación es más largo que el umbral para el cálculo de las proporciones se coge todo el tiempo disponible
    t_init_prop_groups=t_I
    }

  #Sacamos todos los nuevos positivos diarios del período de estimaciones.
  positives_by_group_last_days=t(sapply(seq(t_init_prop_groups,t_0-1,by='days'),daily_positive_by_group))
  
  group_proportions=prop.table(apply(positives_by_group_last_days,2,sum)[-dim(positives_by_group_last_days)[2]])
  
  new_positives=matrix(rep(group_proportions,length(custom_scenario)),nrow=length(custom_scenario),byrow=T)

  for(row in 1:length(custom_scenario)){
    new_positives[row,]=custom_scenario[row]*group_proportions
    residual_row=new_positives[row,]-floor(new_positives[row,])
    new_positives[row,]=floor(new_positives[row,])
    patients_to_add=custom_scenario[row]-sum(new_positives[row,])
    if(patients_to_add>0){
      index_to_add=sort(residual_row,decreasing=T,index.return=T)$ix[1:patients_to_add]
      new_positives[row,index_to_add]=new_positives[row,index_to_add]+1
      }
    }

  #Exportación de los nuevos positivos
  write.table(new_positives,file=paste(carpetaResultados,'nuevos_positivos_escenario.csv',sep=""),row.names = F, col.names=F,sep=',')
}

  
positives_by_group_last_days=t(sapply(seq(t_0,t_1,by='days'),daily_positive_by_group))
positives_by_group_last_days=positives_by_group_last_days[,-dim(positives_by_group_last_days)[2]]
  
write.table(positives_by_group_last_days,file=paste(carpetaResultados,'nuevos_positivos_reales.csv',sep=""),row.names = F, col.names=F,sep=',')
  

##############
# Generación del histórico de positivos, hospital y UCI
##############

daily_situation=function(date_input){
  n_pos=length(which(data$positive==date_input))
  n_in_hos=length(which(data$adm_hos<=date_input & (data$dis_hos>date_input | is.na(data$dis_hos))))
  n_in_ICU=length(which(data$adm_ICU<=date_input & (data$dis_ICU>date_input | is.na(data$dis_ICU))))
  return(c(n_pos,n_in_hos,n_in_ICU))
}

initial_date=as.Date(min(apply(data[,4:8],2,function(x) min(x,na.rm=T))))
final_date=as.Date(max(apply(data[,4:8],2,function(x) max(x,na.rm=T))))

situation_total=as.data.frame(seq(initial_date,final_date,by='days'))
situation_total=cbind(situation_total,t(sapply(seq(initial_date,final_date,by='days'),daily_situation)))
colnames(situation_total)=c('date','positives','hospital','ICU')

write.table(situation_total,file=paste(carpetaResultados,'situation_counts.csv',sep=""),row.names = F, col.names=T,sep=',')

##############
# Generación del histórico de positivos, hospital y UCI por grupo
##############

daily_situation_group=function(date_input){
  result=c()
  for(group in levels(data$group_2)){
    data_aux=data[which(data$group_2==group),]
    n_pos=length(which(data_aux$positive==date_input))
    n_in_hos=length(which(data_aux$adm_hos<=date_input  & (data_aux$dis_hos>date_input | is.na(data_aux$dis_hos))))
    n_in_ICU=length(which(data_aux$adm_ICU<=date_input & (data_aux$dis_ICU>date_input | is.na(data_aux$dis_ICU))))
    result=c(result,n_pos,n_in_hos,n_in_ICU)
  }
  return(result)
}

initial_date_g=as.Date(min(apply(data[,4:8],2,function(x) min(x,na.rm=T))))
final_date_g=as.Date(max(apply(data[,4:8],2,function(x) max(x,na.rm=T))))

situation_total_g=as.data.frame(seq(initial_date_g,final_date_g,by='days'))
situation_total_g=cbind(situation_total_g,t(sapply(seq(initial_date_g,final_date_g,by='days'),daily_situation_group)))

#Create colname vectot
rest_colnames=c()
for(group in levels(data$group_2)){
  rest_colnames=c(rest_colnames,paste(c('positives','hospital','ICU'),toString(group),sep="_"))
}

colnames(situation_total_g)=c('date',rest_colnames)
write.table(situation_total_g,file=paste(carpetaResultados,'situation_counts_groups.csv',sep=""),row.names = F, col.names=T,sep=',')




######################
## Creación situación inicial
######################

# Extraemos para los que están en el sistema los códigos de gravedad de los pacientes.
#Finalmente les calculamos su status y el número de días desde el último cambio de estatus

# Genero la variable status_paciente, que dice de qué tipo es cada paciente a fecha_inicio. -1 es que no está en el sistema,
# 1=positivo pero no ha ido al hospital, 2=hospitalizado pero no ha ido a uci (en ese momento),
# 3=uci, 4=ha salido de uci pero aún hospitalizado.

index_1=which(data$positive<t_0)
index_2=which(data$adm_hos<t_0)
index_3=which(data$adm_ICU<t_0)
index_4=which(data$dis_ICU<t_0)
index_5=which(data$dis_hos<t_0)


status_vector=rep(-1,dim(data)[1])
status_vector[index_1]=1
status_vector[index_2]=2
status_vector[index_3]=3
status_vector[index_4]=4
status_vector[index_5]=-1

#Genero la variable tiempo hasta ese status, que se calculará de manera diferente en función del mismo.
status_time=rep(NA,length(status_vector))

status_time[index_1]=t_0-1-data$positive[index_1]
status_time[index_2]=t_0-1-data$adm_hos[index_2]
status_time[index_3]=t_0-1-data$adm_ICU[index_3]
status_time[index_4]=t_0-1-data$dis_ICU[index_4]

data_initial=cbind(data$group_1,data$group_2,status_vector,status_time)
data_initial=data_initial[which(status_vector>0),]

table_status=table(status_vector[which(status_vector!=-1)])
warning_data_init=paste('*** In the initial day t_0 there are:',sum(table_status[c(2,4)]),' patients in hospital Non-ICU and',sum(table_status[3]),' in ICU. ***')

list_warnings=c(warning_data_init,list_warnings)
list_warnings=c(list_warnings,warnings())
# Exportación de la situación inicial para los ya positivos
write.table(data_initial,file=paste(carpetaResultados,'datos_iniciales.csv',sep=""),row.names = F, col.names=F,sep=',')

##############
## Warnings ##
##############
#Exportamos los warnings
write.table(list_warnings,file=paste(carpetaResultados,'warnings.csv',sep=""),row.names = F, col.names=F,sep=',')
print(list_warnings)


