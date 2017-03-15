# Instruction {#instruction}

<pre class="textdiagram" id="orda.Istruction.1word">
  |<------------------- Destination ----------------------------->|<--------------------- Source -------------------------------->|
  |                                                               |                                                               |
  |                                                       | T |S/L|                                                       | T |S/L|
  +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
  |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |
  +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
  | 31| 30| 29| 28| 27| 26| 25| 24| 23| 22| 21| 20| 19| 18| 17| 16| 15| 14| 13| 12| 11| 10| 9 | 8 | 7 | 6 | 5 | 4 | 3 | 2 | 1 | 0 |
</pre>



* S/L - short or long value format, Long means that an additional word brings additional 32 bits to the value.
* T - type, Imm|Reg

|Bit 16 S/L | Bit 0 S/L | Number of words  |
| :-------: | :-------: | :--------------: |
|     0     |     0     |       1          |
|     0     |     1     |       2          |
|     1     |     0     |       2          |
|     1     |     1     |       3          |

When _Type_ is _Reg_ then the value means resource (register) location.
10 bits for Cell ID and 6/38 bits for Register ID, total number of addressable cells is 2^10=1024.

Immidiate value width can be 16 or 16+32=48 bits. If a data has more than 48 bits then the data has to be
split into multiple instructions. 
