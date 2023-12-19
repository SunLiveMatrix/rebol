REBOL [
	Title: "Cross-Platform Rebol Code Editor"
	File: "code.r"
	Author: "Francois Jouen"
	Email: "fjouen@free.fr"
	Copyright: "(c) 2006, Fran˜ois Jouen"
	Rights: {BSD License

Copyright (c) 2006, Fran˜ois Jouen
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.

    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in
      the documentation and/or other materials provided with the
      distribution.

    * Neither the name of the <ORGANIZATION> nor the names of its
      contributors may be used to endorse or promote products derived
      from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER
OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.}
]


sv*: system/view
ssize: sv*/screen-face/size
xmax: ssize/1 - 200
ymax: ssize/2 - 200
set 'app-dir what-dir 
count: tn-lines: n-char: tn-pages: tn-words: tn-paras: tn-files: tn-chars: n-page: 0
n-line: pindex: n-line-page: tn-pages: n-char: 1
font-size: 14
line-height: 16
tmp-list: []
tmp-files: []
xy: 0x35
tr: 128
cl: to-tuple  reduce [255 0 0 tr]
cx: xmax - 251
cy: line-height + 4
bxs: to-pair reduce [cx cy]


line-list: make sv*/line-info []

lcursor: copy [ fill-pen cl
				pen none
				box 0x0 bxs ]
 	
quit-requested: does [
	if (confirm/with "Really quit this program ?" ["Yes" "No"]) [quit]
	
]

Read-Config: does [
	if error? try [
	initfile: read/Lines join app-dir "editor.ini"
	rebol-location: initfile/1]; evaluateur rebol
	[rebol-location: system/options/boot]	
]

Write-Config: does [
	write join app-dir "editor.ini" rebol-location
]

Select-Rebol: does [
	dest: request-text/title/default " Where is Rebol  ? " to-string rebol-location
	if not none? dest [
		if not empty? dest [
			rebol-location: to-string dest
			Write-Config 
		Read-Config]
	]
]

Run: does [
	if error? try [
		str: join "Loading " fname
		fl: flash str wait 0.5
		call reduce [rebol-location " " Fname] 
		unview/only fl]
		[unview/only fl]
]

About_Box: layout [
	
	space 0x15
	across
	H2 200x48 "Cross-Platform Rebol Code Editor" center wrap
	
	return
	credits: text white navy bold center 200x60
	"Brought to the Rebol Community by Fran˜ois Jouen/Rebol France <C> 2006"

	return
	
 pad 80 btn "OK" [hide-popup]


]

Calculate-pages: does [
	clear lcount/text
	;calculate line height
	line-height: font-size + second (current/para/margin) 
	
	;calculate total number of lines
	tmp: (second (size-text current) / line-height) - 2
	for count 0 tmp 1 [append lcount/text join count + 1 newline]
	append lcount/text tmp + 2
	tn-lines: tmp + 1
	;get number of line per page
	n-line-page: round (current/size/y / line-height) 
	
	;get total number of pages 
	tn-pages: round tn-lines / n-line-page
	tmp: remainder tn-lines n-line-page
	if tmp  <> 0 [tn-pages: tn-pages + 1]
	tn-chars: to-integer length? current/text
	tn-pages: tn-pages + 1
	show [Lcount]
]


Get-File-Info: does [
	 Calculate-pages
	 n-page: 0
	 current/text: head current/text
	 lcount/text: head lcount/text
	 current/line-list: lcount/line-list: none
	 ; get some information about file
	 tn-chars: length? current/text
	 tn-words: parse current/text none
	 tn-paras: parse/all current/text "^/"
	 SBar2/text: join "Length: " tn-chars
	 n-line: n-char: 0
	 if error? try [result: textinfo current line-list n-line] [result: none]
	 sbar3/text: join "Ln " [n-line + 1 ", " line-list/num-chars " chars"]
	 sbar4/text: n-page + 1
	 sbar5/text: tn-pages
	 show [Lcount SBar2 Sbar3 SBar4 Sbar5 bx]	
	

]
update-scroller: func [lines] [
	
	
	if lines [either sl/data = 0 [n-page 0 nline: 0] 
		[n-line: to-integer sl/data * (tn-lines) 
		n-page: to-integer sl/data * (tn-pages - 1)]
	]
	
	if error? try [line-list/num-chars: 0 result: textinfo current line-list n-line] [result: none]
	sbar3/text: join "Ln " [n-line + 1 ", " line-list/num-chars " chars"]
	sbar4/text: n-page + 1
	sbar5/text: tn-pages
	
	y: round (second (current/size) - line-height * sl/data) 
	limite: current/size/y - (line-height + 4)
	if y >= limite  [y: limite]  
	bx/offset: current/offset + as-pair 0 y
	
	scroll-para lcount sl
	current/para/scroll/y: lcount/para/scroll/y
	show [ bx lcount sl sbar3 sbar4 sbar5 current]
	focus sl
	 
]





get-key: func [akey]
[
	
	if tn-chars > 0 [
	 	 
	 
	 
	tmp: to-integer length? sv*/caret
	n-char: (tn-chars - tmp) + 1
	cc: pick current/text n-char

	Calculate-pages
	
			if equal? akey 'end  [sl/data: 1 
					n-line: tn-lines n-char: tmp n-page: tn-pages - 1 
					update-scroller false] 
			        
				
			if equal? akey 'home  [sl/data: 0  
					n-line: 0 n-char: 1 n-page: 0
					update-scroller false]
					
			if equal? akey 'page-down [
					either n-page >= tn-pages [npage: tn-pages - 1] [n-page: n-page + 1]
					n-line: n-line + n-line-page 
					if (n-line > tn-lines)  [n-line: tn-lines]
					sl/data: n-line / tn-lines
					update-scroller false
					]
					
			if equal? akey 'page-up [
					either n-page <= 0 [n-page: 0] [n-page: n-page - 1]
					n-line: n-line - n-line-page 
					if (n-line <= 0)  [n-line: 0]
					sl/data: n-line / tn-lines
					update-scroller false
					]
					
					
			if equal? akey 'down [ 
					n-line: n-line + 1
					if (n-line > tn-lines) [n-line: tn-lines]
					sl/data: n-line / tn-lines
					update-scroller false
					n-line2: mod n-line n-line-page
				    if equal? n-line2 0 [n-page: n-page + 1]
			]
				
			
			
			if equal? akey 'up [
					n-line: n-line - 1
					if n-line <= 0 [n-line: 0 n-page: 1]
					sl/data: n-line / tn-lines
					update-scroller false
					n-line2: mod n-line n-line-page
					if equal? n-line2 0 [n-page: n-page - 1]
			]
			
			if equal? akey 'right [
			 	cc: pick current/text n-char
			]
			if equal? akey 'left [
			 	cc: pick current/text n-char
			]
			
			]
]
Read-File: does [
		clear current/text
		clear lcount/text
		current/text: read to-file fname
		set [path file] split-path fname
		SBar1/text: to-local-file fname
		current/line-list: lcount/line-list: none
		sl/data: 0
		update-scroller false
		n-line: n-char: 1
		show [SBar1 sl current]
	
]
Save-File: does [
	if tn-files > 0[
	if (confirm/with join "Save File " [FName " ?"] ["Yes" "No"]) [write to-file fname current/text]
	]
	
]

Write-File: does [
if tn-files > 0[
	afile: request-file/file/filter/save Fname "*.r"
	if not none? afile [sfname: first afile write to-file sfname current/text]
]
]

New-File: does [
	afile: request-file/filter/save "*.r"
	if not none? afile [
		fname: first afile
		set [path file] split-path first afile
		clear current/text
		clear lcount/text
		buf: reform join "REBOL [" [ newline "Title: Demo" newline
								"File: " to-local-file fname newline
								"Author: " system/user/name newline
								"Email: " system/user/email newline
								"Date: " now newline
								"]" newline]
		
		append current/text buf
		write to-file fname current/text
		append tmp-list first afile
		buffer: copy current/text
		append/only tmp-files buffer
		set [path file] split-path first afile
		append Fliste/data file
		append clear FListe/picked file
		tn-files: tn-files + 1
		show FLIste
		Get-File-Info
		n-line: 8 n-char: length? buf
		sbar3/text: join "Ln " [n-line ", Ch " n-char] 
		SBar1/text: to-local-file fname
		show [SBar1 SBar3]
		focus current
	]
]

Open-File: does [
	afile: request-file/filter "*.r"
	if not none? afile [	
		if error? try [
		forall afile [
		fname: first afile
		Read-File
		buffer: copy current/text
		append/only tmp-files buffer
		set [path file] split-path first afile
		append tmp-list afile
		append Fliste/data file
		append clear FListe/picked file
		Get-File-Info
		focus current
		sv*/caret: head current/text
		show [current Fliste]
		tn-files: tn-files + 1]
		] 
		[Alert "Error"]
		]
		
]

Select-File: does [
		
		clear current/text
		clear lcount/text
		buffer: pick tmp-files pindex 
		current/text: copy buffer
		set [path file] split-path fname
		SBar1/text: to-local-file fname
		current/line-list: lcount/line-list: none
		sl/data: 0
		update-scroller false
		n-line: n-char: 1
		show [SBar1 sl current]
		Get-File-Info
		focus current
		sv*/caret: head current/text
]

Close-File: does  [
	if tn-files > 0 [ 
	if (confirm/with join "Close file " [fname " ?"]  ["Yes" "No"]) [
	clear current/text
	clear LCount/text
	current/line-list: lcount/line-list: none
	remove at tmp-list pindex
	remove at tmp-files pindex
	remove at FListe/data pindex
	tn-files: tn-files - 1
	either tn-files > 0 [
	either tn-files > 1 [pindex: tn-files] [ pindex: 1]
	FName: pick tmp-list pindex Select-File
	append clear FListe/picked pick FListe/data pindex 
	] [hide bx]
	show [current lcount FListe]]
]
]


find-text: func [s /local atext] [
	
	either all [
		not atext: find next sv*/caret s
		not atext: find current/text s] 
		[Alert join s " not found" unfocus return none  ]
		[
		tmp: length? atext   
		
		n-char: (tn-chars - tmp) + 1
		
		sv*/caret: atext
		sv*/highlight-start: sv*/caret
		sv*/highlight-end: skip sv*/highlight-start length? s
		
		xy: (caret-to-offset current atext) - current/para/scroll
		lcount/para/scroll/y: current/para/scroll/y: second min 0x0 current/size / 2 - xy
		
		n-line: to-integer (xy/y / (font-size + 2)) + 1
		
		ratio: n-line / tn-lines
		sl/data: ratio
		show [current lcount sl]
		sbar3/text: join "Ln " [n-line ", Ch " n-char]
		show SBar3
		]
	
]




Resize-Window: does [
	ssize: sv*/screen-face/size
	xmax: ssize/1 - 200
	ymax: ssize/2 - 200
	abox/size: MainWin/size
	ToolBar/size/x: MainWin/size/x 
	FListe/size/x: 190
	FListe/resize/y MainWin/size/y - 75
	FListe/offset: 5x35
	LCount/size/x: 40
	Lcount/size/y: Lcount/parent-face/size/y - 75
	LCount/Offset: 195x35
	Current/size/x: MainWin/size/x - 235 - 16
	Current/size/y: MainWin/size/y - 75
	Current/Offset: 235x35
	sl/size/y: MainWin/size/y - 75
	sl/offset: as-pair (235 + Current/size/x) 35
	SBar1/Size/x: 235
	SBar1/offset: as-pair 0 MainWin/size/y - 35
	SBar2/offset: as-pair 240 MainWin/size/y - 35
	SBar3/offset: as-pair 345 MainWin/size/y - 35
	SBar4/offset: as-pair 450 MainWin/size/y - 35
	SBar5/offset: as-pair 485 MainWin/size/y - 35
	SBar6/offset: as-pair 520 MainWin/size/y - 35
	SBar7/offset: as-pair 625 MainWin/size/y - 35
	cx: MainWin/size/x - 251
	cy: line-height
	bxs: to-pair reduce [cx cy]

	bx/size/x: cx bx/size/y: cy
	show [abox Fliste ToolBar Lcount Current sl SBar1 SBar2 SBar3 SBar4 SBar5 SBar6 SBar7]
	if tn-files > 0 [show bx]
]


MainWin: layout [
	
	across
	origin 0x0
	at 0x0 abox: box as-pair xmax ymax + 70 ivory
	space 5x5
	at 0x0 ToolBar: box as-pair xmax 30
	at 5x35 FListe: text-list as-pair 190 Ymax black white [pindex: face/cnt 
									FName: pick tmp-list pindex Select-File]
	at 195x35 lcount: area as-pair 40 YMax right white white font [size: font-size]
	
	at 235x35 current: area as-pair (xmax - 235 - 16) ymax  white white wrap font [size: font-size]	
	at as-pair 235 + (xmax - 235 - 16) 35 sl: slider as-pair 16 ymax [if tn-files > 0 [update-scroller true]]
	at as-pair 0 (ymax + 40) SBar1: info as-pair 235  30  left  "No File" font [size: 10] wrap
	at as-pair 240 (ymax + 40) SBar2: info as-pair 100 30 center "" font [size: 10] 
	at as-pair 345 (ymax + 40) SBar3: info as-pair 100 30 center "" font [size: 10]
	at as-pair 450 (ymax + 40) SBar4: info as-pair 30 30 center "" font [size: 10]
	at as-pair 485 (ymax + 40) SBar5: info as-pair 30 30 center "" font [size: 10]
	at as-pair 520 (ymax + 40) SBar6: info as-pair 100 30 center mold now/date font [size: 10] 
	at as-pair 625 (ymax + 40) SBar7: info as-pair 50 30 
				center mold now/time font [size: 10] with [rate: 1]
				
	at 5x5 
	b1: btn "New" [New-File]
	b2: btn 50 "Open" [Open-File]
	b3: btn 50 "Close" [Close-File]
	b4: btn 50 "Save" [Save-File]
	b5: btn 50 "Save as" [Write-File]
	
	b6: btn 50 "Find" [if tn-files > 0 [show current Find-text qr/text]]
	qr: field 150 "Rebol" white white [unfocus focus current sv*/caret: head current/text]
	b7: btn 50 "Run" [if tn-files > 0 [Run]]
	b8: btn "Preferences" [Select-Rebol]
	b9: btn "About" [ if error? try [inform/title About_Box "Hello"] [inform About_Box]]
	bt10: btn "Help" [if error? try [do-thru http://www.rebol.com/view/tools/word-browser.r] ["No connected to the Internet"]]
	b11: btn "Quit" [quit-requested]
	at 235x35 bx: box  as-pair (xmax - 251) (line-height + 4) effect [draw lcursor] 
	
] 
Read-Config
center-face MainWin
view/new/options MainWin [resize] 
deflag-face current tabbed; permet les tabulations dans la visualisation
hide bx 




insert-event-func [
		
		switch event/type [
			key          [get-key event/key]       	
			time         [Sbar7/text: mold now/time show SBar7 Calculate-pages]                 	
			resize       [Resize-Window]
        	maximize 	 [Resize-Window]
        	;restore	 []
        	scroll-line []
        	;scroll-n-page []
        	
		]
		if all [event/type = 'alt-down] []
		if all [event/type = 'close event/face = MainWin][quit-requested] 
		event
]

do-events