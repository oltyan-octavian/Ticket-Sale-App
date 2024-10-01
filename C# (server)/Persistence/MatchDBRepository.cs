using System;
using System.Collections.Generic;
using System.Data;
using Model;
using log4net;
using log4net.Util;

namespace Persistence
{
    public class MatchDBRepository : IMatchRepository
    {
        private static readonly ILog logger = LogManager.GetLogger("MatchDBRepository");

        IDictionary<String, string> props;

        public MatchDBRepository(IDictionary<String, string> props)
        {
            logger.Info("Creating MatchDBRepository");
            this.props = props;
        }

        public IEnumerable<Match> findAll()
        {
            logger.Info("Finding all matches");
            IList<Match> matches = new List<Match>();
            IDbConnection connection = DBUtils.getConnection(props);
            using (var command = connection.CreateCommand())
            {
                command.CommandText = "select id,availableSeats,team1ID, team2ID, type, seatPrice from matches";
                using (var result = command.ExecuteReader())
                {
                    while (result.Read())
                    {
                        int id = result.GetInt32(0);
                        int availableSeats = result.GetInt32(1);
                        int team1ID = result.GetInt32(2);
                        int team2ID = result.GetInt32(3);
                        string type = result.GetString(4);
                        int seatPrice = result.GetInt32(5);
                        Match match = new Match(id, type, availableSeats, team1ID, team2ID, seatPrice);
                        
                        matches.Add(match);
                    }
                }
            }

            logger.InfoExt(matches);
            return matches;
        }

        public Match findOne(int id)
        {
            logger.Info("Finding match with id " + id);
            IDbConnection connection = DBUtils.getConnection(props);
            using (var command = connection.CreateCommand())
            {
                command.CommandText = "select id,availableSeats, team1ID, team2ID, type, seatPrice from matches where id=@id";
                IDbDataParameter paramId = command.CreateParameter();
                paramId.ParameterName = "@id";
                paramId.Value = id;
                command.Parameters.Add(paramId);
                using (var result = command.ExecuteReader())
                {
                    if (result.Read())
                    {
                        int idv = result.GetInt32(0);
                        int availableSeats = result.GetInt32(1);
                        int team1ID = result.GetInt32(2);
                        int team2ID = result.GetInt32(3);
                        string type = result.GetString(4);
                        int seatPrice = result.GetInt32(5);
                        Match match = new Match(idv, type, availableSeats, team1ID, team2ID, seatPrice);

                        logger.InfoExt(match);
                        return match;
                    }
                }
            }

            return null;
        }

        public Match save(Match entity)
        {
            logger.Info("Saving match with id " + entity.id);
            IDbConnection connection = DBUtils.getConnection(props);
            try
            {
                
            using (var command = connection.CreateCommand())
            {
                command.CommandText = "INSERT INTO matches(availableSeats, team1ID, team2ID, type, seatPrice) VALUES (@AvailableSeats, @Team1ID, @Team2ID, @Type, @SeatPrice)";
                IDbDataParameter paramSeats = command.CreateParameter();
                paramSeats.ParameterName = "@AvailableSeats";
                paramSeats.Value = entity.availableSeats;
                command.Parameters.Add(paramSeats);
                IDbDataParameter param1id = command.CreateParameter();
                param1id.ParameterName = "@Team1ID";
                param1id.Value = entity.team1ID;
                command.Parameters.Add(param1id);
                IDbDataParameter param2id = command.CreateParameter();
                param2id.ParameterName = "@Team2ID";
                param2id.Value = entity.team2ID;
                command.Parameters.Add(param2id);
                IDbDataParameter paramtype = command.CreateParameter();
                paramtype.ParameterName = "@Type";
                paramtype.Value = entity.type;
                command.Parameters.Add(paramtype);
                IDbDataParameter paramprice = command.CreateParameter();
                paramprice.ParameterName = "@SeatPrice";
                paramprice.Value = entity.seatPrice;
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

        public Match delete(int id)
        {
            throw new System.NotImplementedException();
        }

        public Match update(Match entity)
        {
            logger.Info("Updating match with id " + entity.id);
            IDbConnection connection = DBUtils.getConnection(props);
            try
            {
                
                using (var command = connection.CreateCommand())
                {
                    command.CommandText = "update matches SET availableSeats=@availableSeats WHERE id=@id";
                    IDbDataParameter paramSeats = command.CreateParameter();
                    paramSeats.ParameterName = "@availableSeats";
                    paramSeats.Value = entity.availableSeats;
                    command.Parameters.Add(paramSeats);
                    IDbDataParameter paramid = command.CreateParameter();
                    paramid.ParameterName = "@id";
                    paramid.Value = entity.id;
                    command.Parameters.Add(paramid);
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
    }
}