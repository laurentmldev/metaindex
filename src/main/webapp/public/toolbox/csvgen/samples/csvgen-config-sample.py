#!/usr/local/bin/python3.8

import sys
import os
import os.path
import random

random.seed(a=123456789)

def buildWeightedList(weightList):
    rstlist=[]
    for curName in weightList:
        curWeight=weightList[curName]
        for i in range(curWeight):
            rstlist+=[curName]
    return rstlist

## dossier d'oeuvre MH
#nom
#lieu
#type 
#constat d'état
#photo
#biblio
#suivi réunions


### corélations
## dates des abayes sont 30 à 60 ans plus tôt

### _id
ID_BASE="archive_"
def fid(entryNb,totalNbEntries,fields):
    idNumberStr='{:04d}'.format(entryNb)
    return ID_BASE+idNumberStr

### liens
# 0=no links, 1=links for everybody
LINKS_RATIO=0.3
### try to produce some link clusters for better demonstration
### the more a document is linked, the more it has chances to be linked
# 0.5=half random links, half reuse, 0=only random links, 1=only reuse (after first one)
CLUSTERING_WEIGHT=0.4
AVG_NB_LINKS=2
targets_weights={}
def fliens(entryNb,totalNbEntries,fields):
    liensStr=""
    if random.randint(1,100)<=(100*LINKS_RATIO):
        targets_list=buildWeightedList(targets_weights)
        nbLiens=random.randint(1,AVG_NB_LINKS*2)        
        for i in range(nbLiens):         
            if len(liensStr)>0:
                liensStr+=","
            targetId=None
            # avoid having twice same target in same document
            while targetId==None or str(targetId) in liensStr:
                
                enforceCluster=random.randint(1,100)
                # resuse existing node already having a link
                if len(targets_list)>0 and enforceCluster/100<=CLUSTERING_WEIGHT:                
                    targetIndex=random.randint(0,len(targets_list)-1)
                    targetId=targets_list[targetIndex]                    
                # create link randomly
                else:
                    targetNb=random.randint(1,totalNbEntries)
                    targetId=fid(targetNb,totalNbEntries,fields)
            
            if targetId not in targets_weights:
                targets_weights[targetId]=1            
            else:
                targets_weights[targetId]+=1
                

            liensStr+=targetId
        
    return liensStr

### pic
NB_PICS=30
PICS_FILE_BASE="document_"
PICS_FILE_EXT=".jpg"
def fpic(entryNb,totalNbEntries,fields):
    picNumber = random.randint(1,NB_PICS)
    picNumberStr='{:04d}'.format(picNumber)
    picName=PICS_FILE_BASE+picNumberStr+PICS_FILE_EXT
    return picName

### résumé
resume_list=[
    "Ex illum minus nam officiis fugit aut reiciendis earum et quidem dicta.",
    "Quo maiores illo quo distinctio possimus ut veniam corporis sed maxime dicta eos quia dolor.",
    "Qui tempora velit nam laboriosam consectetur ea similique necessitatibus At quia velit non distinctio maiores est dolorem quaerat.",
    "Non dicta ratione sed quia voluptates non fugit aperiam aperiam ipsum aut omnis autem ut magni ratione. ",
    "Sed fugiat corporis ad fugit voluptate non cumque tenetur eos necessitatibus eius.",
    "Et sequi ea asperiores porro ut exercitationem animi sed exercitationem quis qui officiis doloribus et perferendis quas! ",
    "Ut distinctio aperiam eos nemo vitae id tempore corporis. Ut porro tempore ut vitae quaerat eos atque dolores ea sint praesentium. ",
    "Quo quaerat omnis quo doloremque sed necessitatibus corporis. Est deserunt illum sed enim cupiditate est sapiente voluptas",
    "Ut alias dicta aut ullam laudantium vel doloremque recusandae quo soluta provident ea tempore dolor et tempore voluptas",
    "Aut nostrum adipisci id odio doloremque qui reprehenderit nostrum rem praesentium facilis non dolore eligendi."
]
def fresume(entryNb,totalNbEntries,fields):
    idx=random.randint(0,len(resume_list)-1)
    curval=resume_list[idx]
    return curval

### transcription
transcription_list=[
    "Lorem ipsum dolor sit amet. Eos earum quas non rerum quia aut exercitationem minus et quod porro et magni modi sit quia perspiciatis. Et Quis cumque vel harum omnis ex autem ipsam qui tenetur saepe id reiciendis dolores et dolorem unde. Sit praesentium alias et ipsum blanditiis sed alias nemo vel odio. ",
    "Id modi architecto At dolore nemo qui sint possimus sit internos molestiae. Qui doloremque voluptatem et necessitatibus cupiditate ut doloremque labore rem corrupti incidunt et ducimus vero et velit modi quo officia quas. Ad debitis quam est quidem earum vel iure animi cum aliquid molestias rem ullam impedit aut explicabo dolorum et natus excepturi. Qui tempore reprehenderit vel soluta illo et labore magnam vel atque doloribus in eveniet delectus",
    "Ab pariatur laudantium consequatur laboriosam in adipisci expedita est Quis magnam At exercitationem repellendus aut odio facere aut possimus aliquam? Et amet voluptatem et accusantium consectetur est fuga voluptatem id obcaecati sunt aut minus Quis sed delectus natus.",
    "Et corrupti quia et eveniet molestiae ut quia rerum rem repellat iste. Sed incidunt recusandae et ullam ullam ut architecto quae eum Quis harum et voluptate galisum ut vero nihil mollitia architecto! Ut assumenda illo ea optio nihil est enim laboriosam ex voluptatibus expedita qui numquam voluptatum non blanditiis omnis quo facilis dolor. ",
    "Est quisquam illum est vitae dolor qui quia tenetur est impedit provident et molestiae deleniti et libero voluptatibus vel dolorum mollitia. Eum soluta autem vel voluptas odit eum sint quaerat! Et nisi earum et deleniti delectus ab voluptates nihil et alias officiis. ",
    "Aut eveniet libero ut cupiditate quisquam ut quod fugiat. Sed iusto voluptas sit consectetur debitis vel ipsum fuga. Qui sint tempore qui atque porro eum assumenda ipsa sit quos nobis est animi eligendi ut similique quas. Aut officia laboriosam id sint nisi aut rerum autem ut pariatur tenetur. A omnis commodi ut saepe quia et accusamus explicabo? Et facilis illum vel eaque maiores ea molestiae debitis et iusto nobis. Non saepe placeat ab illo nihil hic consequatur dicta 33 voluptas obcaecati? Sit galisum quia sit esse aperiam in assumenda sint qui dolores consequatur ab itaque fugiat nam sunt autem. Eum magni nihil et ipsum eveniet in quas exercitationem?",
    "In blanditiis quia aut internos maiores a galisum asperiores sed dolor nostrum aut distinctio adipisci non eaque porro et molestiae natus. 33 doloremque laudantium ut facere rerum et quod atque. Qui voluptas eveniet ut voluptatem soluta est officia facere. In optio Quis ut repudiandae repellat vel nemo doloribus. Cum labore voluptate quo assumenda sit fugiat sequi in exercitationem suscipit sit deleniti reiciendis. Ut autem exercitationem eos omnis maiores qui excepturi iste et quasi magnam et eveniet illo sit mollitia dolorum. Qui praesentium magni At enim possimus et ducimus magnam sit tempore esse id omnis natus eum dolore quis? Ad temporibus Quis 33 quia quisquam aut voluptates quis eos voluptatibus odit et facere molestiae id alias cupiditate. In voluptas reprehenderit est consequatur quibusdam est dicta nulla. Aut nulla laborum et nihil libero sit recusandae saepe. Et quam repellat et ducimus enim non dolorum quidem qui voluptatem enim aut omnis sint in placeat corporis sit sunt perspiciatis. ",
    "Eum fugiat eius At Quis aspernatur rem tenetur voluptatem nam maxime adipisci vel ratione magni sed quaerat eveniet aut magni officiis? Non quis nemo eum mollitia blanditiis aut maxime exercitationem et quaerat tempore et pariatur rerum ut harum vitae. Id voluptatum iste aut ullam labore qui quam repellendus sed enim praesentium vel dolor voluptatem aut ratione sapiente. Ut mollitia eveniet id saepe perspiciatis et voluptatem repellat ut alias eaque qui nihil dolor. Et voluptatem ipsum qui obcaecati tenetur et facilis minima et magnam quasi eos explicabo quasi At rerum amet. Est quia voluptas nam dolorem magni et suscipit quia aut voluptates molestiae. Ex galisum natus est rerum quasi ut magni autem qui consequatur assumenda ea molestiae accusamus ut expedita repellat. Rem mollitia quia ut dolores deserunt aut reprehenderit quia non dolores suscipit ut perspiciatis sint! ",
    "Quo nobis architecto est accusantium eius vel nihil culpa et impedit autem aut quos tempora ut voluptas cupiditate. Est asperiores ducimus nam quam aliquid cum veritatis voluptates ut perferendis amet sit nesciunt autem. Est fugiat reiciendis qui tempore asperiores et obcaecati internos voluptatem expedita eos voluptatem assumenda. Qui dolor velit et aliquam sint et sapiente cupiditate aut illum quasi. In sint maiores qui obcaecati dolores quo perspiciatis unde ut eaque illum ea voluptatem beatae qui labore iste in recusandae quis. Sit rerum dignissimos ea dolore repellat ea nobis error. Vel aspernatur voluptatem est illum doloremque qui obcaecati placeat vel quod labore sed enim voluptates sed accusamus eaque aut nihil quaerat. Aut natus explicabo sit velit numquam qui dolorum quibusdam qui atque odio aut consequatur dolor! Quo sequi dolorum et blanditiis voluptatibus et quis galisum. Ut accusamus pariatur in fuga dignissimos aut vero fugit non architecto modi est velit minus eos voluptatum dolores et molestiae consequatur? Ut fugit delectus et quasi quasi est sunt excepturi et consequatur velit rem quis tempora in quod rerum ut numquam doloremque. Qui delectus ipsam ut totam quod sed autem obcaecati 33 exercitationem error? Quo dolorem asperiores hic dolorem repudiandae eos quia enim ea vitae omnis eos quisquam molestias. Qui eveniet deserunt et repellat aliquam ut deserunt tempora sed iste eius. ",
    "Non voluptatum quis est molestiae quisquam ab illo accusantium. Id neque aliquam nam molestiae suscipit ex nobis molestiae est impedit minima aut excepturi omnis qui veniam molestias eum esse deleniti. In repellat internos eum perspiciatis error est veritatis ullam est cupiditate suscipit non dolores maxime ea quaerat saepe. Quo voluptas accusamus est minus vitae ab culpa unde non incidunt itaque et voluptatem error. Est voluptatum accusantium et labore culpa ut officiis autem vel earum porro! ",
    "Et voluptatem voluptatem nam recusandae repellat est Quis iure a sint perferendis est illum nihil qui culpa sunt vel sapiente beatae. Ad aspernatur libero qui dolorem possimus et repudiandae sunt qui explicabo rerum. Et saepe necessitatibus est corporis veniam est reiciendis architecto eos necessitatibus alias? Et debitis esse aut molestias dignissimos aut quae dolorem qui nihil nihil et sint aperiam hic nemo nemo et velit deserunt! Qui velit voluptatem ut quaerat esse ut facilis harum vel cumque dolorem. Eos libero quae voluptatem dignissimos eos alias rerum aut quod beatae aut ratione commodi eos necessitatibus quae ut modi omnis. Ea impedit voluptas qui aliquid quia et optio doloribus est autem debitis 33 cupiditate provident cum ipsa repellat quo harum consectetur. Est sapiente voluptatem ut fugit omnis est perspiciatis sint aut officiis laudantium. Qui consequatur tempora in voluptatem mollitia vel numquam quia id quia praesentium aut architecto quisquam. "
]
def ftranscription(entryNb,totalNbEntries,fields):
    idx=random.randint(0,len(transcription_list)-1)
    curval=transcription_list[idx]
    return curval

### type d'acte
type_acte_list=buildWeightedList({ "rente":4, "compte":1,"minute_notariale":1 })
def ftype_acte(entryNb,totalNbEntries,fields):
    idx=random.randint(0,len(type_acte_list)-1)
    curval=type_acte_list[idx]
    return curval

### type producteur
type_producteur_list=buildWeightedList({ "commanderie":3, "seigneurie":1,"judiciaire":7 })
def ftype_producteur(entryNb,totalNbEntries,fields):
    idx=random.randint(0,len(type_producteur_list)-1)
    curval=type_producteur_list[idx]
    return curval

### lieu de conservation
weightsList={ "Archives Nationales":2,
                          "AD Franche-Comté":7,
                          "Abbaye de Fontenay":2,
                          "Abbaye de Pontigny":3,
                          "Abbaye de Saint-Philibert":1,
                          "Abbaye de Notre-Dame-de-Quincy":1
                    }
#lieux_conservation_list=buildWeightedList(weightsList)
def flieux_conservation(entryNb,totalNbEntries,fields):
    myWeightsList=weightsList.copy()
    if fields["type_acte"]=="minute_notariale":
        myWeightsList["Archives Nationales"]=weightsList["Archives Nationales"]*random.randint(2,6)
        myWeightsList["AD Franche-Comté"]=weightsList["AD Franche-Comté"]*random.randint(2,4)
    else:
        myWeightsList["Abbaye de Fontenay"]=weightsList["Abbaye de Fontenay"]*random.randint(2,9)
        myWeightsList["Abbaye de Pontigny"]=weightsList["Abbaye de Pontigny"]*random.randint(2,9)
        myWeightsList["Abbaye de Saint-Philibert"]=weightsList["Abbaye de Saint-Philibert"]*random.randint(2,9)
        myWeightsList["Abbaye de Notre-Dame-de-Quincy"]=weightsList["Abbaye de Notre-Dame-de-Quincy"]*random.randint(2,9)

    lieux_conservation_list=buildWeightedList(myWeightsList)
    idx=random.randint(0,len(lieux_conservation_list)-1)
    curval=lieux_conservation_list[idx]
    return curval



### cote
cote_zone1_list=[ "ZTG ","XIEd-","jjd / K3 ","AAAPR12 ","BAAPR//34 ","TTH-","OIII:","XIII ","IV ","XVI"]
cote_zone2_list=[ "YY ","56-","42//","bb11:","Z "]
cote_zone3_list=[ "","","","bis ","ter ","// "]
def fcote(entryNb,totalNbEntries,fields):    
    cote=""
    idx=random.randint(0,len(cote_zone1_list)-1)
    cote+=cote_zone1_list[idx]
    idx=random.randint(0,len(cote_zone2_list)-1)
    cote+=cote_zone2_list[idx]
    idx=random.randint(0,len(cote_zone3_list)-1)
    cote+=cote_zone3_list[idx]
    number=random.randint(0,9999)    
    cote+=str(number)
    
    return cote


### date
DATE_MIN=1500
DATE_MAX=1700
def fdate(entryNb,totalNbEntries,fields):
    year=random.randint(DATE_MIN,DATE_MAX)
    if fields["lieux_conservation"].startswith("Abbaye"):
        year=year-random.randint(30,60)

    if random.randint(1,10)>3:
        day='{:02d}'.format(random.randint(1,29))
        month='{:02d}'.format(random.randint(1,12))
        curDate=day+"/"+month+"/"+str(year)
    else:
        curDate=str(year)

    return curDate

    

fields= { "_id":fid ,
          "pic":fpic,
          "type_acte":ftype_acte,
          "type_producteur":ftype_producteur,
          "lieux_conservation":flieux_conservation,
          "cote":fcote,
          "date":fdate,
          "liens":fliens,
          "resume":fresume,
          "transcription":ftranscription
          }
    