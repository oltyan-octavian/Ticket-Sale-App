using System;
using System.Collections.Generic;
using Model;

namespace Service
{
    public interface IService
    {
        ICollection<string> GetListMatches(string search);

        User Login(string username, string password, Observer client);

        void Logout(string username);

        MatchDTO GetMatch(int i);

        bool AddTicket(BuyTicketsDTO ticket);
    }
}