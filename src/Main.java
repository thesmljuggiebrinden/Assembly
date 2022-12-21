import java.io.IOException;
import java.util.*;
import java.util.Scanner;
public class Main
{
    // ADD		RD, RS	add registers RD+RS, store the result in RD
    // SUB		RD, RS	subtract registers RD-RS, store the result in RD
    // LOAD		RD,[RS]	load 1 byte from memory at address RS into RD
    // STORE	RD,[RS]	store 1 byte from RD into memory at address RS
    // SKIPNZ	RD		if RD!=0, skip the next instruction
    // SKIPZ	RD		if RD==0, skip the next instruction
    // JALR		RD,RS		save PC+1 into RD, jump to RS
    // SLI		RD, IMM
    //          shift the lower four bits of RD into the upper four bits, then load 4-
    //bit IMM into the lower four bits of RD
    //HALT				stops the processor

    // NOP				do nothing
    // NAND		RD, RS	bitwise nand of RD and RS, save result to RD
    // PUSH		RD		decrement SP, then save RD to memory[SP]
    // POP		RD
    //          load memory[SP] from the stack into RD, then increment SP
    // OUT		RD		print RD to the console
    // IN		RD		read from the console into RD
    // INC		RD		add one to RD
    // DEC		RD		subtract one from RD
    // SKIPL	RD		if RD is negative (high bit is 1), skip the next
    //                  instruction
    // SKIPGE	RD		if RD is positive (high bit is 0), skip

    /*
    A=00, B=01, C=10, D=11

    ADD		0 1 1 1 RD1 RD0 RS1 RS0
					example: ADD B,C = 0 1 1 1 0 1 1 0 = 0x76
    SUB		0 0 0 1 RD1 RD0 RS1 RS0
					example: SUB D,D = 0 0 0 1 1 1 1 1 = 0x1F
    NAND	0 1 1 0 RD1 RD0 RS1 RS0
					example: NAND D,D = 0 1 1 0 1 1 1 1 = 0x1F
    LOAD	0 0 1 0 RD1 RD0 RS1 RS0
					example: LOAD C,[B] = 0 0 1 0 1 0 0 1 = 0x29
    STORE	0 0 1 1 RD1 RD0 RS1 RS0
					example: STORE C,[B] = 0 0 1 1 1 0 0 1 = 0x29
    SKIPZ	0 1 0 0 RD1 RD0  0 0
					example SKIPZ C = 0 1 0 0 1 0 0 0 = 0x48
    SKIPNZ	0 1 0 0 RD1 RD0  0 1
    SKIPL	0 1 0 0 RD1 RD0  1 0
    SKIPGE	0 1 0 0 RD1 RD0  1 1
    JALR	0 1 0 1 RD1 RD0 RS1 RS0
					example: JALR C,D = 0 1 0 1 1 0 1 1 = 0x5B
    SLI		1 1 IMM3 IMM2 RD1 RD0 IMM1 IMM0
					example: SLI C, 9 = 1 1 1 0 1 0 0 1 = 0xE9
    HALT	0 0 0 0 0 0 0 1
					example: HALT = 0 0 0 0 0 0 1 = 0x01
    IN		1 0 0 0 RD1 RD0  1 1
    OUT		1 0 0 0 RD1 RD0  1 0
    INC		1 0 0 0 RD1 RD0  0 0
    DEC		1 0 0 0 RD1 RD0  0 1
    PUSH	1 0 1 0 RD1 RD0  0 0
    POP		1 0 1 0 RD1 RD0  0 1

     */

    /*; program takes num from mem[FE], multiplies it by 3, saves in mem[FF]

        #start
    ; read from memory[fe]
    SLI	D,15
    SLI	D,14
    LOAD	A,[D]

    ; A will be number from mem[fe], B is product, C counts down from 3
    SUB	B,B
    SLI	C,0
    SLI	C,3

        #loop
    ; if c is 0, goto #done
    SLIU	D,#done
    SLIL	D,#done
    SKIPNZ	C
    JALR	D,D

            ; prod = prod + number
    ADD	B,A

            ; dec C
    SLI	D,0
    SLI	D,1
    SUB	C,D

            ; go to loop
    SLIU	D,#loop
    SLIL	D,#loop
    JALR	D,D

#done
    ; save result
    SLI	D,15
    SLI	D,15
    STORE	B,[D]

    ; stop processor
    HALT

     */

    public static int PC = 0, SP = 0;





    public static int[] program = new int[]
            {
                    0xff,
                    0xfe,
                    0x23,
                    0x15,
                    0xc8,
                    0xcb,
                    0xcd,
                    0xcd,
                    0x49,
                    0x5f,
                    0x74,
                    0xcc,
                    0xcd,
                    0x1b,
                    0xcc,
                    0xde,
                    0x5f,
                    0xff,
                    0xff,
                    0x37,
                    0x01,

            };
   public static int[] memory = new int[256];
   public static int[] registers = new int[4];



    public static void main(String[] args) throws IOException
    {
        for (int i = 0; i < program.length; i++)
        {
            memory[i] = program[i];
        }
        registers[0] = 0;
        registers[1] = 0;
        registers[2] = 0;
        registers[3] = 0;
        PC = 0;
        memory[0xfe] = 2;
        memory[253] = 3;
        for (int i = 0; i < 100; i++)
        {
            int instructions = memory[PC];
            PC = PC + 1;
            PC = PC & 0b11111111;
            int firstfourbits = (instructions>>4) & 0b1111;
            int firsttwobits = (instructions>>6) & 0b11;
            int lasttwobits = (instructions) & 0b11;
            int RD = (instructions>>2) &3 & 0b11;
            int RS = instructions & 0b11;
            int IMM = ((instructions>>2) & 0b1100) | (instructions & 0b11);


            if (firsttwobits == 0b11)
            {
                // SLI instruction
                int sliresult = registers[RD] << 4;
                sliresult = sliresult | IMM;
                sliresult = sliresult & 0b11111111;
                registers[RD] = sliresult;
                System.out.printf("PC = %x, inst = %x, op = SLI,  A = %x, B = %x, C = %x, D = %x\n",
                        PC, instructions, registers[0],registers[1],registers[2],registers[3]);
            }
            else if (firstfourbits == 0b0111)
            {
                // ADD instruction
                registers[RD] = (registers[RD] + registers[RS]) & 0b11111111;
                System.out.printf("PC = %x, inst = %x, op = ADD, A = %x, B = %x, C = %x, D = %x \n",
                        PC, instructions, registers[0],registers[1],registers[2],registers[3]);
            }
            else if (firstfourbits == 0b0001)
            {
                // SUB instruction
                registers[RD] = (registers[RD] - registers[RS]) & 0b11111111;
                System.out.printf("PC = %x, inst = %x, op = SUB, A = %x, B = %x, C = %x, D = %x \n",
                        PC, instructions, registers[0],registers[1],registers[2],registers[3]);
            }
            else if (firstfourbits == 0b0110)
            {
                // NAND instruction
                registers[RD] = registers[RD] & registers[RS];
                registers[RD] = -registers[RD];
                System.out.printf("PC = %x, inst = %x, op = NAND, A = %x, B = %x, C = %x, D = %x \n",
                        PC, instructions, registers[0],registers[1],registers[2],registers[3]);

            }
            else if (firstfourbits == 0b0010)
            {
                // LOAD instruction
                int address = registers[RS];
                registers[RD] = memory[address];
                System.out.printf("PC = %x, inst = %x, op = LOAD, A = %x, B = %x, C = %x, D = %x \n",
                        PC, instructions, registers[0],registers[1],registers[2],registers[3]);

            }
            else if (firstfourbits == 0b0011)
            {
                // STORE instruction
                memory[registers[RS]] = registers[RD];
                System.out.printf("PC = %x, inst = %x, op = STORE, A = %x, B = %x, C = %x, D = %x \n",
                        PC, instructions, registers[0],registers[1],registers[2],registers[3]);
            }
            else if (firstfourbits == 0b0100)
            {
                if (lasttwobits == 0b00)
                {
                    // SKIPZ instruction
                    System.out.println("SKIPZ");
                     if (registers[RS] == 0)
                     {
                         PC = (PC + 1) & 0b11111111;
                     }
                     System.out.printf("PC = %x, inst = %x, op = SKIPZ, A = %x, B = %x, C = %x, D = %x \n",
                            PC, instructions, registers[0],registers[1],registers[2],registers[3]);


                }
                else if (lasttwobits == 0b01)
                {
                    // SKIPNZ instruction
                    if (registers[RD] != 0)
                    {
                        PC = (PC + 1) & 0b11111111;
                    }
                    System.out.printf("PC = %x, inst = %x, op = SKIPNZ, A = %x, B = %x, C = %x, D = %x \n",
                            PC, instructions, registers[0],registers[1],registers[2],registers[3]);
                }
                else if (lasttwobits == 0b10)
                {
                    // SKIPL instruction
                    if (registers[RS] < 0)
                    {
                        PC = (PC + 1) & 0b11111111;
                    }
                    System.out.printf("PC = %x, inst = %x, op = SKIPL, A = %x, B = %x, C = %x, D = %x \n",
                            PC, instructions, registers[0],registers[1],registers[2],registers[3]);
                }
                else if (lasttwobits == 0b11)
                {
                    // SKIPGE instruction
                    if (registers[RS] > 0)
                    {
                        PC = (PC + 1) & 0b11111111;
                    }
                    System.out.printf("PC = %x, inst = %x, op = SKIPGE, A = %x, B = %x, C = %x, D = %x \n",
                            PC, instructions, registers[0],registers[1],registers[2],registers[3]);
                }
            }
            else if (firstfourbits == 0b0101)
            {
                // JALR instruction
                int tmp = registers[RD];
                registers[RD] = PC;
                PC = tmp;
                System.out.printf("PC = %x, inst = %x, op = JALR, A = %x, B = %x, C = %x, D = %x \n",
                        PC, instructions, registers[0],registers[1],registers[2],registers[3]);
            }
            else if (firstfourbits == 0b1000)
            {
                if (lasttwobits == 0b11)
                {
                    // IN instruction
                    System.in.read();
                    System.out.printf("PC = %x, inst = %x, op = IN, A = %x, B = %x, C = %x, D = %x \n",
                            PC, instructions, registers[0],registers[1],registers[2],registers[3]);

                }
                else if (lasttwobits == 0b10)
                {
                    // OUT instruction
                    System.out.println(registers[RD]);
                    System.out.printf("PC = %x, inst = %x, op = OUT, A = %x, B = %x, C = %x, D = %x \n",
                            PC, instructions, registers[0],registers[1],registers[2],registers[3]);
                }
                else if (lasttwobits == 0b00)
                {
                    // INC instruction
                    PC = PC + 1;
                    System.out.printf("PC = %x, inst = %x, op = INC, A = %x, B = %x, C = %x, D = %x \n",
                            PC, instructions, registers[0],registers[1],registers[2],registers[3]);
                }
                else if (lasttwobits == 0b01)
                {
                    // DEC instruction
                    PC = PC - 1;
                    System.out.printf("PC = %x, inst = %x, op = DEC, A = %x, B = %x, C = %x, D = %x \n",
                            PC, instructions, registers[0],registers[1],registers[2],registers[3]);
                }
            }
            else if (firstfourbits == 0b1010)
            {
                if (lasttwobits == 0b00)
                {
                    // PUSH instruction
                    SP--;
                    registers[RD] = memory[SP];
                    System.out.printf("PC = %x, inst = %x, op = PUSH, A = %x, B = %x, C = %x, D = %x \n",
                            PC, instructions, registers[0],registers[1],registers[2],registers[3]);
                }
                else if (lasttwobits == 0b01)
                {
                    // POP instruction
                    memory[SP] = registers[RD];
                    SP++;
                    System.out.printf("PC = %x, inst = %x, op = POP, A = %x, B = %x, C = %x, D = %x \n",
                            PC, instructions, registers[0],registers[1],registers[2],registers[3]);
                }
            }
            else if (instructions == 0b00000001)
            {
                // HALT instruction
                System.out.printf("PC = %x, inst = %x, op = HALT, A = %x, B = %x, C = %x, D = %x \n",
                        PC, instructions, registers[0],registers[1],registers[2],registers[3]);
                System.out.println("memory[ff] is "+ memory[0xff]);
                break;
            }

        }
    }
}