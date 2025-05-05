#ifndef HEADER_HPP_INCLUDED
#define HEADER_HPP_INCLUDED

class produs{
     private:
    char nume[100];
    double pret;
    int cant;
    char tara[100];
    char info[100];
     public:
        char* getnume();

        double getpret();

        int getcant();

        char* gettara();

        char* getinfo();

        void setnume(char *s);

        void setpret(double k);

        void setcant(int k);

        void settara(char *s);

        void setinfo(char *s);

        friend std::istream& operator>>(std::istream& , produs& );

        friend std::ostream& operator<<(std::ostream& , produs&  );

        friend std::ostream& salvfisier(std::ostream&  ,produs& );

};

#endif // HEADER_HPP_INCLUDED
