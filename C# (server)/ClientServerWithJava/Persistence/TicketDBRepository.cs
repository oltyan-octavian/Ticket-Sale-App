using System;
using System.Collections.Generic;
using System.Data;
using Model;
using log4net;
using log4net.Util;

namespace Persistence
{
    public class TicketDBRepository : ITicketRepository
    {
        private static readonly ILog logger = LogManager.GetLogger("TicketDBRepository");

        IDictionary<String, string> props;

        public TicketDBRepository(IDictionary<String, string> props)
        {
            logger.Info("Creating TicketDBRepository");
            this.props = props;
        }

        public IEnumerable<Ticket> findAll()
        {
            logger.Info("Finding all tickets");
            IList<Ticket> tickets = new List<Ticket>();
            IDbConnection connection = DBUtils.getConnection(props);
            using (var command = connection.CreateCommand())
            {
                command.CommandText = "select id,matchID, price, clientName, numberOfSeats from tickets";
                using (var result = command.ExecuteReader())
                {
                    while (result.Read())
                    {
                        int id = result.GetInt32(0);
                        int matchID = result.GetInt32(1);
                        int price = result.GetInt32(2);
                        string clientName = result.GetString(3);
                        int numberOfSeats = result.GetInt32(4);
                        Ticket ticket = new Ticket(id, matchID, price, clientName, numberOfSeats);
                        tickets.Add(ticket);
                        
                    }
                }
            }

            logger.InfoExt(tickets);
            return tickets;
        }

        public Ticket findOne(int id)
        {
            logger.Info("Finding ticket with id " + id);
            IDbConnection connection = DBUtils.getConnection(props);
            using (var command = connection.CreateCommand())
            {
                command.CommandText = "select id, matchID, price, clientName, numberOfSeats from tickets where id=@id";
                IDbDataParameter paramId = command.CreateParameter();
                paramId.ParameterName = "@id";
                paramId.Value = id;
                command.Parameters.Add(paramId);
                using (var result = command.ExecuteReader())
                {
                    if (result.Read())
                    {
                        int idv = result.GetInt32(0);
                        int matchID = result.GetInt32(1);
                        int price = result.GetInt32(2);
                        string clientName = result.GetString(3);
                        int numberOfSeats = result.GetInt32(4);
                        Ticket ticket = new Ticket(idv, matchID, price, clientName, numberOfSeats);
                        logger.InfoExt(ticket);
                        return ticket;
                        
                    }
                }
            }

            return null;
        }

        public Ticket save(Ticket entity)
        {
            logger.Info("Saving ticket with id " + entity.id);
            IDbConnection connection = DBUtils.getConnection(props);
            try
            {
                
                using (var command = connection.CreateCommand())
                {
                    command.CommandText = "INSERT INTO tickets(matchID, price, clientName, numberOfSeats) VALUES (@match, @price, @client, @seats)";
                    IDbDataParameter paramSeats = command.CreateParameter();
                    paramSeats.ParameterName = "@seats";
                    paramSeats.Value = entity.numberOfSeats;
                    command.Parameters.Add(paramSeats);
                    IDbDataParameter paramid = command.CreateParameter();
                    paramid.ParameterName = "@match";
                    paramid.Value = entity.matchID;
                    command.Parameters.Add(paramid);
                    IDbDataParameter paramclient = command.CreateParameter();
                    paramclient.ParameterName = "@client";
                    paramclient.Value = entity.clientName;
                    command.Parameters.Add(paramclient);
                    IDbDataParameter paramprice = command.CreateParameter();
                    paramprice.ParameterName = "@price";
                    paramprice.Value = entity.price;
                    command.Parameters.Add(paramprice);
                    command.ExecuteNonQuery();
                    return entity;
                }
            }
            catch (Exception e)
            {
                logger.Error(e);
                return null;
            }

        }

        public Ticket delete(int id)
        {
            throw new System.NotImplementedException();
        }

        public Ticket update(Ticket entity)
        {
            throw new System.NotImplementedException();
        }
    }
}