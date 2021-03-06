@echo off
set _apphome="G:\Google Drive\Eclipse\Apps"
set _br=0
set _ch=0
set _de=0
set _ee=0
set _fr=0
set _it=0
set _uk=0
set _us=0

if "%1" == "" (
   set _br=1
   set _ch=1
   set _de=1
   set _ee=1
   set _fr=1
   set _it=1
   set _uk=1
   set _us=1
)

if /I "%1"=="BR" (Set _br=1)
if /I "%1"=="CH" (Set _ch=1)
if /I "%1"=="DE" (Set _de=1)
if /I "%1"=="EE" (Set _ee=1)
if /I "%1"=="FR" (Set _fr=1)
if /I "%1"=="IT" (Set _it=1)
if /I "%1"=="UK" (Set _uk=1)
if /I "%1"=="US" (Set _us=1)

if %_br%==1 (
   echo Processing BR
   %_apphome%"\RegexProcessor.jar" -i ".\BR-2020\NCSS - BR [2020] (Strict).txt" -r "ComparedNCSSs - Rules.xlsx" -s "BR Script.txt" -f "<{CAT}#{SUB}#{GRP}#{TRM}>"
   @del BR-2020.log
   @ren RegexProcessor.log BR-2020.log
)
if %_ch%==1 (
   echo Processing CH
   %_apphome%"\RegexProcessor.jar" -i ".\CH-2016\NCSS - CH [2016] (Strict).txt" -r "ComparedNCSSs - Rules.xlsx" -s "CH Script.txt" -f "<{CAT}#{SUB}#{GRP}#{TRM}>"
   @del CH-2016.log
   @ren RegexProcessor.log CH-2016.log
)
if %_de%==1 (
   echo Processing DE
   %_apphome%"\RegexProcessor.jar" -i ".\DE-2016\NCSS - DE [2016] (Strict).txt" -r "ComparedNCSSs - Rules.xlsx" -s "DE Script.txt" -f "<{CAT}#{SUB}#{GRP}#{TRM}>"
   @del DE-2016.log
   @ren RegexProcessor.log DE-2016.log
)
if %_ee%==1 (
   echo Processing EE
   %_apphome%"\RegexProcessor.jar" -i ".\EE-2019\NCSS - EE [2019] (Strict).txt" -r "ComparedNCSSs - Rules.xlsx" -s "EE Script.txt" -f "<{CAT}#{SUB}#{GRP}#{TRM}>"
   @del EE-2019.log
   @ren RegexProcessor.log EE-2019.log
)
if %_fr%==1 (
   echo Processing FR
   %_apphome%"\RegexProcessor.jar" -i ".\FR-2018\NCSS - FR [2018] (Strict).txt" -r "ComparedNCSSs - Rules.xlsx" -s "FR Script.txt" -f "<{CAT}#{SUB}#{GRP}#{TRM}>"
   @del FR-2018.log
   @ren RegexProcessor.log FR-2018.log

   %_apphome%"\RegexProcessor.jar" -i ".\FR-2015\NCSS - FR [2015] (Strict).txt" -r "ComparedNCSSs - Rules.xlsx" -s "FR Script.txt" -f "<{CAT}#{SUB}#{GRP}#{TRM}>"
   @del FR-2015.log
   @ren RegexProcessor.log FR-2015.log
)
if %_it%==1 (
   echo Processing IT
   %_apphome%"\RegexProcessor.jar" -i ".\IT-2017\NCSS - IT [2017] (Strict).txt" -r "ComparedNCSSs - Rules.xlsx" -s "IT Script.txt" -f "<{CAT}#{SUB}#{GRP}#{TRM}>"
   @del IT-2017.log
   @ren RegexProcessor.log IT-2017.log
)
if %_uk%==1 (
   echo Processing UK
   %_apphome%"\RegexProcessor.jar" -i ".\UK-2016\NCSS - UK [2016] (Strict).txt" -r "ComparedNCSSs - Rules.xlsx" -s "UK Script.txt" -f "<{CAT}#{SUB}#{GRP}#{TRM}>"
   @del UK-2016.log
   @ren RegexProcessor.log UK-2016.log
)
if %_us%==1 (
   echo Processing US
   %_apphome%"\RegexProcessor.jar" -i ".\US-2018\NCSS - US [2018] (Strict).txt" -r "ComparedNCSSs - Rules.xlsx" -s "US Script.txt" -f "<{CAT}#{SUB}#{GRP}#{TRM}>"
   @del US-2018.log
   @ren RegexProcessor.log US-2018.log

   %_apphome%"\RegexProcessor.jar" -i ".\US-2015\NCSS - US [2015] (Strict).txt" -r "ComparedNCSSs - Rules.xlsx" -s "US Script.txt" -f "<{CAT}#{SUB}#{GRP}#{TRM}>"
   @del US-2015.log
   @ren RegexProcessor.log US-2015.log
)
