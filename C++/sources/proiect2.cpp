#include <iostream>
#include <string.h>
#include <conio.h>
#include <stdlib.h>
#include <stdio.h>
#include <windows.h>
#include <fstream>
#include "./headers/header.hpp"
using namespace std;

        char* produs::getnume()
        {
            return this->nume;
        };
        double produs::getpret()
        {
            return this->pret;
        };
        int produs::getcant()
        {
            return this->cant;
        };
        char* produs::gettara()
        {
            return this->tara;
        };
        char* produs::getinfo()
        {
            return this->info;
        };
        void produs::setnume(char *s)
        {
            strcpy(this->nume,s);
                if ((strlen(this->nume) > 0) && (this->nume[strlen (this->nume) - 1] == '\n'))
                    this->nume[strlen (this->nume) - 1] = '\0';
        };
        void produs::setpret(double k)
        {
            this->pret=k;
        };
        void produs::setcant(int k)
        {
            this->cant=k;
        };
        void produs::settara(char *s)
        {
            strcpy(this->tara,s);
                if ((strlen(this->tara) > 0) && (this->tara[strlen (this->tara) - 1] == '\n'))
                    this->tara[strlen (this->tara) - 1] = '\0';
        };
        void produs::setinfo(char *s)
        {
            strcpy(this->info,s);
                if ((strlen(this->info) > 0) && (this->info[strlen (this->info) - 1] == '\n'))
                    this->info[strlen (this->info) - 1] = '\0';
        };
         istream&  operator>>(istream& in, produs& produs)
        {
            in.getline(produs.nume,100);
            if ((strlen(produs.nume) > 0) && (produs.nume[strlen (produs.nume) - 1] == '\n'))
                    produs.nume[strlen (produs.nume) - 1] = '\0';
            in>>produs.pret;
            in>>produs.cant;
            in.ignore();
            in.getline(produs.tara,100);
            if ((strlen(produs.tara) > 0) && (produs.tara[strlen (produs.tara) - 1] == '\n'))
                    produs.tara[strlen (produs.tara) - 1] = '\0';
            in.getline(produs.info,100);
            if ((strlen(produs.info) > 0) && (produs.info[strlen (produs.info) - 1] == '\n'))
                    produs.info[strlen (produs.info) - 1] = '\0';
            return in;
        };
        ostream& operator<<(ostream& out, produs& produs)
        {
        out << "Nume produs: " << produs.nume << "\n";
        out << "Pret produs: " << produs.pret << "\n";
        out << "Cantitate produs: " << produs.cant << "\n";
        out << "Tara de provenienta: " << produs.tara << "\n";
        out << "Informatii produs: " << produs.info << "\n";
        return out;
        };
         ostream&  salvfisier(ostream& out,produs& produs)
        {
            out<<"\n";
        out<<produs.nume<<endl;
        out<<produs.pret<<endl;
        out<<produs.cant<<endl;
        out<<produs.tara<<endl;
        out<<produs.info<<endl;
        return out;
        };

produs produse[100];
int n=0;
void afisarefuncti()
{
    cout<<"Apelati programul folosind gramatica:"<<endl<<"Nume_program nr_functie argumente_functie"<<endl;
    cout<<"Lista de functi(argumentele cu tipul lor necesar in paranteza):"<<endl;
    cout<<"1.afisare()\n2.adaugare prod(int poz,char nume[], double pret,int cant,char tara[],char info[])\n3.stergere prod(int poz)\n4.Add cantitate(int poz, int cant)\n5.Afisare prod cumparator()\n6.cautare prod nume(char nume[])\n7.cautare prod tara(char tara[])8.vizualizare info(int poz)\n9.cumparare prod(int poz,int cant)\n10.edit nume(int poz, char nume[])\n11.edit pret(int poz, double pret)\n12.edit cant(int poz, int cant)\n13.edit tara(int poz, char tara[])\n14.edit info(int poz, char info[])";
}
void salvarefisier()
{
    ofstream fp("./files/produs.txt");
    fp<<"#NUMAR DE ELEMENTE\n";
    fp<<n<<"\n";
    fp<<"\n";
    fp<<"#NUME PRODUS\n";
    fp<<"#COST numar de tip double\n";
    fp<<"#CANTITATE numar de tip natural\n";
    fp<<"#TARA DE PROVENIENTA\n";
    fp<<"#INFORMATI DESPRE PRODUS\n";
    for(int i=0;i<n;i++)
    {   salvfisier(fp,produse[i]);
    }
    fp.close();
}
void citireproduse()
{
    ifstream fp("./files/produs.txt");
    if(fp.fail())
    {
    salvarefisier();
    }
else{
        char citire[100];

    fp.getline(citire,100);
    fp>>n;
    fp.ignore();
    fp.ignore();
    fp.getline(citire,100);
    fp.getline(citire,100);
    fp.getline(citire,100);
    fp.getline(citire,100);
    fp.getline(citire,100);

    for(int i=0;i<=n;i++)
    {   fp.ignore();
        fp>>produse[i];}
    fp.close();
}
}
void afisareproduse()
{
     for(int i = 0; i < n; i++) {
        cout << "#####################################################################################\n";
        cout << "Numar produs: " << i << "\n";
        cout << produse[i];
    }
}
void afisareprodusecump()
{
   int k = 1;
   for(int i = 0; i < n; i++) {
    if(produse[i].getcant() > 0) {
        k = 0;
        cout << "#####################################################################################\n";

        cout << "Numar produs: " << i << "\n";
        cout << produse[i];
    }
}
    if(k)
    cout << "Nu au fost gasite produse valabile\n";
}

void adaugareprodus(int poz,char nume[], double pret,int cant,char tara[],char info[])
{


    if (poz>n)
    {
        cout << "pozitie invalida";
        getch();

    }
    else {

    for(int i=n;i>poz;i--)
    {produse[i].setpret(produse[i-1].getpret());
    produse[i].setcant(produse[i-1].getcant());
    produse[i].setnume(produse[i-1].getnume());
    produse[i].settara(produse[i-1].gettara());
    produse[i].setinfo(produse[i-1].getinfo());
    }

    if ((strlen(nume) > 0) && (nume[strlen (nume) - 1] == '\n'))
        nume[strlen (nume) - 1] = '\0';
    produse[poz].setnume(nume);
    produse[poz].setpret(pret);
    produse[poz].setcant(cant);
    if ((strlen(tara) > 0) && (tara[strlen (tara) - 1] == '\n'))
        tara[strlen (tara) - 1] = '\0';
    produse[poz].settara(tara);
    if ((strlen(info) > 0) && (info[strlen (info) - 1] == '\n'))
        info[strlen (info) - 1] = '\0';
    produse[poz].setinfo(info);
    n=n+1;
    salvarefisier();}
}
void editareprodusnume(int i,char s[])
{
    produse[i].setnume(s);
    salvarefisier();
}
void editareproduspret(int i,double k)
{
    produse[i].setpret(k);
    salvarefisier();
}
void editareproduscant(int i,int k)
{
    produse[i].setcant(k);
    salvarefisier();
}
void editareprodustara(int i,char s[])
{
    produse[i].settara(s);
    salvarefisier();
}
void editareprodusinfo(int i,char s[])
{
    produse[i].setinfo(s);
    salvarefisier();
}
void stergereprodus(int poz)
{
    if (poz>=n)
    {
        cout << "pozitie invalida";
        getch();

    }
    else {
    for(int i=poz;i<n-1;i++)
    {produse[i].setpret(produse[i+1].getpret());
    produse[i].setcant(produse[i+1].getcant());
    produse[i].setnume(produse[i+1].getnume());
    produse[i].settara(produse[i+1].gettara());
    produse[i].setinfo(produse[i+1].getinfo());
    }
    n=n-1;
    salvarefisier();}
}
void cautareprodnume(char s[])
{
    int i=0,j,k2=1;
    char s2[100],*pt;
    if ((strlen(s) > 0) && (s[strlen (s) - 1] == '\n'))
            s[strlen (s) - 1] = '\0';
    while (s[i])
    {
        s[i]=tolower(s[i]);
        i++;
    }
    for (i=0;i<n;i++)
    {
        strcpy(s2,produse[i].getnume());
        j=0;
        while (s2[j])
        {
            s2[j]=tolower(s2[j]);
            j++;
        }
        pt=strstr(s2,s);
        if (pt!=NULL)
        {   k2=0;
            cout<<"Numar produs:"<<i<<endl;
            cout<<"Cantitate produs:"<<produse[i].getcant()<<endl;
            cout<<"Nume produs: ";
            for (int k=0;k<strlen(s2)-strlen(pt);k++)
                cout<<s2[k];
            //printf("\033[0;33m");
            cout<<s;
            //printf( "\033[0m");
            for (int k=0;k<strlen(pt)-strlen(s);k++)
                cout<<pt[k+strlen(s)];
            cout<<endl;;
        }
    }
    if(k2)
        cout<<"Nu au fost gasite produse";
    getch();
}
void cautareprodtara(char s[])
{
    system("cls");
    int i=0,j,k2=1;
    char s2[100],*pt;
    if ((strlen(s) > 0) && (s[strlen (s) - 1] == '\n'))
            s[strlen (s) - 1] = '\0';
    while (s[i])
    {
        s[i]=tolower(s[i]);
        i++;
    }
    for (i=0;i<n;i++)
    {
        strcpy(s2,produse[i].gettara());
        j=0;
        while (s2[j])
        {
            s2[j]=tolower(s2[j]);
            j++;
        }
        pt=strstr(s2,s);
        if (pt!=NULL)
        {
            k2=0;
            cout<<"Numar produs: "<<i<<endl;
            cout<<"Cantitate produs:"<<produse[i].getcant()<<endl;
            cout<<"Tara produs: ";
            for (int k=0;k<strlen(s2)-strlen(pt);k++)
                cout<<s2[k];
            //printf("\033[0;33m");
            cout<<s;
            //printf( "\033[0m");
            for (int k=0;k<strlen(pt)-strlen(s);k++)
                cout<<pt[k+strlen(s)];
            cout<<endl;
        }
    }
    if(k2)
        cout<<"Nu au fost gasite produse";

    getch();
}
void vizualizareinformati(int i)
{   system("cls");

    if (i>=n)
    {
        cout << "pozitie invalida";
        getch();

    }
    else{
        cout << produse[i];
        getch();
    }
}
void cumparareprodus(int i,int k,char num[]=NULL)
{
    system("cls");
    if (i>=n)
    {
        cout << "pozitie invalida";
        getch();

    }
    else{
        if (k>produse[i].getcant())
        {
        cout<<"nu exista suficiente produse";
        getch();

        }
        else{
            produse[i].setcant(produse[i].getcant()-k);
            char fact[100];
            if(num!=NULL)
                strcpy(fact,num);
                else strcpy(fact,"user");
            strcat(fact,"-factura.txt");
            ofstream o(fact);
            o<<"Nume produs:"<<produse[i].getnume()<<endl;
            o<<"Pret produs:"<<produse[i].getpret()<<endl;
            o<<"Cantitate produs cumparat:"<<k<<endl;
            o<<"Pret total:"<<produse[i].getpret()*k<<endl;
            o.close();
            cout<<"Produsul sa cumparat cu succes";
            //if (produse[i].cant==0)
                //{for(i;i<n-1;i++)
                    //{produse[i].pret=produse[i+1].pret;
                    //produse[i].cant=produse[i+1].cant;
                    //strcpy(produse[i].nume,produse[i+1].nume);
                    //strcpy(produse[i].tara,produse[i+1].tara);
                    //strcpy(produse[i].info,produse[i+1].info);
                    //}
                    //n=n-1;

                    //}

        }
    }
    salvarefisier();
}
void adaugarecantitate(int i,int k)
{
    if (i>=n)
    {
        cout << "pozitie invalida";
        getch();

    }
    else{
        produse[i].setcant(produse[i].getcant()+k);
        salvarefisier();
    }
}
int main(int argc,char *argv[])
{
    if(argc==1)
        afisarefuncti();
    else{citireproduse();

        switch(atoi(argv[1]))
        {


            case 1:
                if(argc!=2)
                cout<<"Apelul nu are numarul argumentelor corect"<<endl<<"Apel corect:Nume_program 1";
                else afisareproduse();

                break;
            case 2:
                if(argc!=8)
                cout<<"Apelul nu are numarul argumentelor corect"<<endl<<"Apel corect:Nume_program 2 pozitie(int) nume pret(double) cantitate(int) tara infomati";
                else
                adaugareprodus(atoi(argv[2]),argv[3],atof(argv[4]),atoi(argv[5]),argv[6],argv[7]);
                break;
            case 3:
                if(argc!=3)
                cout<<"Apelul nu are numarul argumentelor corect"<<endl<<"Apel corect:Nume_program 3 pozitie(int)";
                else
                stergereprodus(atoi(argv[2]));
                break;
            case 4:
                if(argc!=4)
                cout<<"Apelul nu are numarul argumentelor corect"<<endl<<"Apel corect:Nume_program 4 pozitie(int) cantitate(int)";
                else
                adaugarecantitate(atoi(argv[2]),atoi(argv[3]));
                break;
            case 5:
                if(argc!=2)
                cout<<"Apelul nu are numarul argumentelor corect"<<endl<<"Apel corect:Nume_program 5";
                else
                afisareprodusecump();
                break;
            case 6:
                if(argc!=3)
                cout<<"Apelul nu are numarul argumentelor corect"<<endl<<"Apel corect:Nume_program 6 sir_de_cautat";
                else
                cautareprodnume(argv[2]);
                break;
            case 7:
                if(argc!=3)
                cout<<"Apelul nu are numarul argumentelor corect"<<endl<<"Apel corect:Nume_program 7 sir_de_cautat";
                else
                cautareprodtara(argv[2]);
                break;
            case 8:
                if(argc!=3)
                cout<<"Apelul nu are numarul argumentelor corect"<<endl<<"Apel corect:Nume_program 8 pozitie(int)";
                else
                vizualizareinformati(atoi(argv[2]));
                break;
            case 9:
                if(argc!=5&&argc!=4)
                cout<<"Apelul nu are numarul argumentelor corect"<<endl<<"Apel corect:Nume_program 9 pozitie(int) cantitate(int) (obtional)nume_cumparator";
                else
                cumparareprodus(atoi(argv[2]),atoi(argv[3]),argv[4]);
                break;
            case 10:
                if(argc!=4)
                cout<<"Apelul nu are numarul argumentelor corect"<<endl<<"Apel corect:Nume_program 10 pozitie(int) nume";
                else
                editareprodusnume(atoi(argv[2]),argv[3]);
                break;
            case 11:
                if(argc!=4)
                cout<<"Apelul nu are numarul argumentelor corect"<<endl<<"Apel corect:Nume_program 11 pozitie(int) pret(double)";
                else
                editareproduspret(atoi(argv[2]),atof(argv[3]));
                break;
            case 12:
                if(argc!=4)
                cout<<"Apelul nu are numarul argumentelor corect"<<endl<<"Apel corect:Nume_program 12 pozitie(int) cantitate(int)";
                else
                editareproduscant(atoi(argv[2]),atoi(argv[3]));
                break;
            case 13:
                if(argc!=4)
                cout<<"Apelul nu are numarul argumentelor corect"<<endl<<"Apel corect:Nume_program 13 pozitie(int) tara";
                else
                editareprodustara(atoi(argv[2]),argv[3]);
                break;
            case 14:
                if(argc!=4)
                cout<<"Apelul nu are numarul argumentelor corect"<<endl<<"Apel corect:Nume_program 14 pozitie(int) info";
                else
                editareprodusinfo(atoi(argv[2]),argv[3]);
                break;
            default:
                exit(-69);

    }
    }
}
